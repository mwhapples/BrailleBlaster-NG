-- Keep DOCX empty paragraphs as empty-paragraph Divs that bbx.lua writes as
-- <bb:emptyParagraph/>.
--
-- docx-page-break.lua drops blank paragraphs, and with them ASM's empty
-- spacer paragraphs next to tables. FixTableSpacing needs to know where those
-- were, so this filter runs first and gives each one text that survives.

local EMPTY_STYLE = "BB Empty Paragraph"
local TEXT = "\u{E000}BBEMPTYPARAGRAPH\u{E000}"

local function emptyParagraph()
  return pandoc.Div({ pandoc.Plain({ pandoc.Str(TEXT) }) },
    pandoc.Attr("", {}, { ["custom-style"] = EMPTY_STYLE }))
end

local function isBlank(block)
  if block.t ~= "Para" and block.t ~= "Plain" then return false end
  for _, inline in ipairs(block.content) do
    if inline.t ~= "Space" and inline.t ~= "SoftBreak" and inline.t ~= "LineBreak"
      and not (inline.t == "Str" and string.match(inline.text, "^%s*$")) then
      return false
    end
  end
  return true
end

-- An empty Word paragraph: a blank Para, or a styled Div holding only one
local function isEmptyParagraph(block)
  if isBlank(block) then return true end
  return block.t == "Div" and #block.content == 1 and isBlank(block.content[1])
end

function Pandoc(doc)
  local blocks = {}
  for _, block in ipairs(doc.blocks) do
    blocks[#blocks + 1] = isEmptyParagraph(block) and emptyParagraph() or block
  end
  return pandoc.Pandoc(blocks, doc.meta)
end
