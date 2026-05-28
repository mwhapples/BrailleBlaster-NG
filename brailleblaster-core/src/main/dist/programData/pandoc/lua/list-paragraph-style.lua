-- Convert DOCX paragraphs with custom-style "List Paragraph" into real Pandoc bullet lists
-- before bbx.lua runs, so they import as BBX list types.

local function customStyle(attr)
  if attr == nil then
    return nil
  end

  local attrs = attr.attributes
  if attrs ~= nil then
    return attrs["custom-style"]
  end

  return nil
end

local function isListParagraphDiv(block)
  if block == nil or block.t ~= "Div" then
    return false
  end

  local style = customStyle(block.attr)
  if style == nil then
    return false
  end

  local normalized = string.lower(style)
  return normalized == "list paragraph" or normalized == "paragraph list"
end

function Pandoc(doc)
  local out = {}
  local i = 1

  while i <= #doc.blocks do
    local block = doc.blocks[i]

    if isListParagraphDiv(block) then
      local items = {}

      while i <= #doc.blocks and isListParagraphDiv(doc.blocks[i]) do
        local div = doc.blocks[i]
        items[#items + 1] = div.content
        i = i + 1
      end

      out[#out + 1] = pandoc.BulletList(items)
    else
      out[#out + 1] = block
      i = i + 1
    end
  end

  return pandoc.Pandoc(out, doc.meta)
end
