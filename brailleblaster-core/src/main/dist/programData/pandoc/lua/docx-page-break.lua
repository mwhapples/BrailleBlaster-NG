-- Turn DOCX page breaks into page-break Divs that bbx.lua writes as
-- <bb:pageBreak/>.
--
-- Pandoc drops Word page breaks, so DocxPageBreaks replaces each one with
-- MARKER text before pandoc reads the file. This filter splits paragraphs at
-- the marker. A marker it cannot split on (inside formatting, a list or a
-- table) puts the break before the block that holds it.

local MARKER = "\u{E000}BBPAGEBREAK\u{E000}"
local BREAK_STYLE = "BB Page Break"

local function pageBreak()
  return pandoc.Div({}, pandoc.Attr("", {}, { ["custom-style"] = BREAK_STYLE }))
end

local function isPageBreak(block)
  return block.t == "Div" and block.attr.attributes["custom-style"] == BREAK_STYLE
end

-- Split a Str into its text pieces and markers.
local function splitStr(text)
  local pieces = {}
  local start = 1
  while true do
    local i, j = string.find(text, MARKER, start, true)
    if i == nil then break end
    if i > start then pieces[#pieces + 1] = pandoc.Str(string.sub(text, start, i - 1)) end
    pieces[#pieces + 1] = "break"
    start = j + 1
  end
  if start <= #text then pieces[#pieces + 1] = pandoc.Str(string.sub(text, start)) end
  return pieces
end

-- Remove markers anywhere inside a block. Returns the block and the number
-- of markers removed.
local function stripMarkers(block)
  local count = 0
  local stripped = pandoc.walk_block(block, {
    Str = function(s)
      local _, n = string.gsub(s.text, MARKER, "")
      if n == 0 then return nil end
      count = count + n
      local text = string.gsub(s.text, MARKER, "")
      return pandoc.Str(text)
    end
  })
  return stripped, count
end

local function addBreaks(out, count)
  for _ = 1, count do out[#out + 1] = pageBreak() end
end

local function isBlank(inlines)
  for _, inline in ipairs(inlines) do
    if inline.t ~= "Space" and inline.t ~= "SoftBreak" and inline.t ~= "LineBreak"
      and not (inline.t == "Str" and inline.text == "") then
      return false
    end
  end
  return true
end

-- Split a Para or Plain at top-level markers.
local function splitParagraph(block, out)
  local current = {}
  local function flush()
    if not isBlank(current) then out[#out + 1] = block.t == "Para" and pandoc.Para(current) or pandoc.Plain(current) end
    current = {}
  end

  for _, inline in ipairs(block.content) do
    if inline.t == "Str" and string.find(inline.text, MARKER, 1, true) then
      for _, piece in ipairs(splitStr(inline.text)) do
        if piece == "break" then
          flush()
          out[#out + 1] = pageBreak()
        else
          current[#current + 1] = piece
        end
      end
    else
      local stripped, count = stripMarkers(pandoc.Plain({ inline }))
      if count > 0 then
        -- the break is inside formatting, so it goes before this text
        flush()
        addBreaks(out, count)
      end
      for _, x in ipairs(stripped.content) do current[#current + 1] = x end
    end
  end
  flush()
end

local processBlocks

-- Split a Div around the breaks in its content, so the breaks stay
-- outside custom-style Divs such as Word's List styles.
local function splitDiv(block, out)
  local segment = {}
  local function flush()
    if #segment > 0 then out[#out + 1] = pandoc.Div(segment, block.attr) end
    segment = {}
  end

  for _, child in ipairs(processBlocks(block.content)) do
    if isPageBreak(child) then
      flush()
      out[#out + 1] = child
    else
      segment[#segment + 1] = child
    end
  end
  flush()
end

processBlocks = function(blocks)
  local out = {}
  for _, block in ipairs(blocks) do
    if block.t == "Para" or block.t == "Plain" then
      splitParagraph(block, out)
    elseif block.t == "Div" then
      splitDiv(block, out)
    else
      local stripped, count = stripMarkers(block)
      addBreaks(out, count)
      out[#out + 1] = stripped
    end
  end
  return out
end

function Pandoc(doc)
  return pandoc.Pandoc(processBlocks(doc.blocks), doc.meta)
end
