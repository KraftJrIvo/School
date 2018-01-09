
function getTextWidth(text, font) {
    // re-use canvas object for better performance
    var canvas = getTextWidth.canvas || (getTextWidth.canvas = document.createElement("canvas"));
    var context = canvas.getContext("2d");
    context.font = font;
    var metrics = context.measureText(text);
    return metrics.width;
}

class TextBlock {    
    constructor(parent, text, speakerName, offX, offY, lang, spriteName, isStart=false) {
        this.x = offX;
        this.y = offY;
        this.lines = [];
        this.spriteName = spriteName;
        for (var z = 0; z < 2; ++z){
            this.lines.push([]);
            var curLine = "";
            for (var i = 0; i < text[z].length; ++i) {
                var c = text[z][i];
                curLine += c;
                if (/*curLine.length == MAX_LINE_SYMBOLS*/getTextWidth(curLine, GAME_FONT) > MAX_LINE_WIDTH) {
                    var l = curLine.length;
                    while (c != ' ' && l > 0) {
                        i--;
                        l--;
                        c = text[z][i];
                        curLine = curLine.substr(0, l);
                    }
                    this.lines[z].push(curLine);
                    curLine = "";
                }
                else if (c == '\n' || i == text[z].length - 1) {
                    this.lines[z].push(curLine);
                    curLine = "";
                }
                if (this.lines[z].length == MAX_LINES) break;
            }
        }
        this.update(lang);
        this.text = text;
        this.parent = parent;
        this.links = [];
        this.linkedTextBlocks = [];
        this.linksPriorities = [];
        this.linksConditions = [];
        this.speakerName = speakerName;
        this.linkRemove = -1;
        this.isStart = isStart;
    }
    
    update(lang) {
        var maxLineWidth = 200;
        for (var i = 0; i < this.lines[lang].length; ++i){
            var lineWidth = getTextWidth(this.lines[lang][i], GAME_FONT);
            if (lineWidth > maxLineWidth) maxLineWidth = lineWidth;
        }
        this.w = maxLineWidth / 3 + 3;
        this.h = this.lines[lang].length * 15 + 4;
    }
	
	drawLinks(offX, offY, lang) {
       ctx.font  = EDITOR_TEXT_FONT;
        for (var i = 0; i < this.links.length; ++i) {
            var x1 = this.x + this.w/2 + offX;
            var y1 = this.y + this.h + offY;
            var x2;
            var y2;
            if (this.linkedTextBlocks[i] == null) {
                x2 = lastMouseX + offX;
                y2 = lastMouseY + offY;
            } else {
                x2 = this.linkedTextBlocks[i].x + this.linkedTextBlocks[i].w/2 + offX;
                y2 = this.linkedTextBlocks[i].y + this.linkedTextBlocks[i].h/2 + offY;
            }
            ctx.strokeStyle = "#000000";
            ctx.beginPath();
            ctx.moveTo(x1-2,y1);
            ctx.lineTo(x2,y2);
            ctx.moveTo(x1+2,y1);
            ctx.lineTo(x2,y2);
            ctx.stroke();
			ctx.fillStyle = "#000000";
            if (this.links[i][lang] == "") {
                ctx.fillText(this.linksConditions[i], x1 + (x2-x1)/2, y1 + (y2-y1)/2);
            } else {
                ctx.fillText(this.links[i][lang], x1 + (x2-x1)/2, y1 + (y2-y1)/2);
            }
            if (this.linkRemove == i) {
                ctx.strokeStyle = "#ff0000";
                ctx.beginPath();
                ctx.moveTo(x1 + (x2-x1)/2 - 5,y1 + (y2-y1)/2 - 5);
                ctx.lineTo(x1 + (x2-x1)/2 + 5,y1 + (y2-y1)/2 + 5);
                ctx.moveTo(x1 + (x2-x1)/2 + 5,y1 + (y2-y1)/2 - 5);
                ctx.lineTo(x1 + (x2-x1)/2 - 5,y1 + (y2-y1)/2 + 5);
                ctx.stroke();
            }
        }
        ctx.font  = EDITOR_FONT;
	}
    
    checkLinks(offX, offY, x, y) {
        for (var i = 0; i < this.links.length; ++i) {
            var x1 = this.x + this.w/2 + offX;
            var y1 = this.y + this.h + offY;
            var x2;
            var y2;
            if (this.linkedTextBlocks[i] == null) {
                x2 = lastMouseX + offX;
                y2 = lastMouseY + offY;
            } else {
                x2 = this.linkedTextBlocks[i].x + this.linkedTextBlocks[i].w/2 + offX;
                y2 = this.linkedTextBlocks[i].y + this.linkedTextBlocks[i].h/2 + offY;
            }
            var dist = Math.sqrt((x1 + (x2-x1)/2 - x)*(x1 + (x2-x1)/2 - x) + (y1 + (y2-y1)/2 - y)*(y1 + (y2-y1)/2 - y));
            if (dist < 5) {
                this.linkRemove = i;
                return i;
            }
        }
        this.linkRemove = -1;
        return -1;
    }
    
    draw(offX, offY, lang) {
        ctx.font  = EDITOR_TEXT_FONT;
        ctx.fillStyle = "#aaaaaa";
        ctx.strokeStyle = "#777777";
        ctx.beginPath();
        ctx.arc(this.x + this.w/2 + offX, this.y + this.h + offY, 8,0,2*Math.PI);
        ctx.stroke();
		ctx.fillRect(this.x + offX, this.y + offY, this.w, this.h);
        ctx.strokeRect(this.x + offX, this.y + offY, this.w, this.h);
        ctx.fillStyle = "#000000";
        for (var i = 0; i < this.lines[lang].length; ++i) {
            ctx.fillText(this.lines[lang][i], this.x + 3 + offX, this.y + 12 + 15 * i + offY);
        }
        //ctx.fillText(this.text, this.x + this.w/2 - 6.5 * this.text.length/2 + 3, this.y + 12);
        if (!this.parent || this.parent.speakerName[lang] != this.speakerName[lang] || this.parent.isStart) {
            ctx.fillText(this.speakerName[lang], this.x + 3 + offX, this.y - 6 + offY);
        }
        if (!this.isStart) {
            ctx.beginPath();
            ctx.moveTo(this.x + this.w + 10 - 3 + offX, this.y - 10 - 3 + offY);
            ctx.lineTo(this.x + this.w + 10 + 3 + offX, this.y - 10 + 3 + offY);
            ctx.moveTo(this.x + this.w + 10 + 3 + offX, this.y - 10 - 3 + offY);
            ctx.lineTo(this.x + this.w + 10 - 3 + offX, this.y - 10 + 3 + offY);
            ctx.stroke();
            ctx.beginPath();
            ctx.moveTo(this.x + this.w + 10 - 3 + offX, this.y + offY);
            ctx.lineTo(this.x + this.w + 10 + 3 + offX, this.y + offY);
            ctx.moveTo(this.x + this.w + 10 + offX, this.y - 3 + offY);
            ctx.lineTo(this.x + this.w + 10 + offX, this.y + 3 + offY);
            ctx.stroke();
            ctx.moveTo(this.x + this.w + 10 - 3 + offX, this.y + 10 - 3 + offY);
            ctx.lineTo(this.x + this.w + 10 + 3 + offX, this.y + 10 - 3 + offY);
            ctx.lineTo(this.x + this.w + 10 + 3 + offX, this.y + 10 + 3 + offY);
            ctx.lineTo(this.x + this.w + 10 - 3 + offX, this.y + 10 + 3 + offY);
            ctx.lineTo(this.x + this.w + 10 - 3 + offX, this.y + 10 - 3 + offY);
            ctx.stroke();
        }
        ctx.font  = EDITOR_FONT;
        var off = 0;
        if (this.flagChange) {
            ctx.fillText("f: " + this.flagId, this.x + offX, this.y + this.h + 10 + offY);
            off += 10;
        }
        if (this.itemTransfer) {
            ctx.fillText("i: " + this.itemName, this.x + offX, this.y + this.h + off + 10 + offY);
        }
    }
}

function draw() {
    ctx.fillStyle = "#aabbcc";
    ctx.fillRect(0, 0, 2000, 2000);
    ctx.fillStyle = "#444488";
    ctx.fillText("globalX: " + -globalOffsetX + " globalY: " + -globalOffsetY, 10, 10);
    for (var i = 0; i < textBlocks.length; ++i) {
        textBlocks[i].update(language);
    }
	for (var i = 0; i < textBlocks.length; ++i) {
        textBlocks[i].drawLinks(globalOffsetX, globalOffsetY, language);
    }
    for (var i = 0; i < textBlocks.length; ++i) {
        textBlocks[i].draw(globalOffsetX, globalOffsetY, language);
    }
}

function createTextBlock(isStart, ret) {
    var text = [document.getElementById('text0').value, document.getElementById('text1').value];
    var textEx = [document.getElementById('textEx0').value, document.getElementById('textEx1').value];
    newTB = new TextBlock(null, text, textEx, -globalOffsetX, -globalOffsetY, language, $('#imgName').val(), isStart);
    if ($('#flagChange').prop('checked')) {
        newTB.flagChange = true;
        newTB.flagCharId = parseInt($('#flagOwnerId').val());
        newTB.flagId = parseInt($('#flagId').val());
        newTB.flagVal = $('#flagTrue').prop('checked');
    }
    if ($('#itemTransfer').prop('checked')) {
        newTB.itemTransfer = true;
        newTB.giveTo = parseInt($('#giveTo').val());
        newTB.itemName = $('#itemName').val();
        newTB.itemsCount = parseInt($('#itemCount').val());
        newTB.canGiveSpeech = [$('#giveText0').val(), $('#giveText1').val()];
        newTB.cantGiveSpeech = [$('#notGiveText0').val(), $('#notGiveText1').val()];
    }
    if (!ret) {
        textBlocks.push(newTB);
    } else {
        return newTB;
    }
}

function copyXML() {
	var result = "<dialog>\r\n";
	for (var i = 0; i < textBlocks.length; ++i) {
		var ctb = textBlocks[i];
        var text1 = ""
        var text2 = ""
        for (var j = 0; j < ctb.lines[0].length; ++j) {
            text1 += ctb.lines[0][j] + "&#10;";
        }
        for (var j = 0; j < ctb.lines[1].length; ++j) {
            text2 += ctb.lines[1][j] + "&#10;";
        }
		result += "  <speechBlock id=\"" + i + "\" textEng=\"" + text1 + "\" textRus=\"" + text2 + "\" speakerNameEng=\"" + ctb.speakerName[0]  + "\" speakerNameRus=\"" + ctb.speakerName[1] + "\">\n";
        result += "    <sprite name=\"" + textBlocks[i].spriteName + "\"/>\n";
        for (var j = 0; j < ctb.links.length; ++j) {
			var link = ctb.links[j];
			var linkedTB = ctb.linkedTextBlocks[j];
			var linkPriority = ctb.linksPriorities[j];
			var linkCondition = ctb.linksConditions[j];
			result += "    <link priority=\"" + linkPriority + "\" textEng=\"" + link[0] + "\" textRus=\"" + link[1] +  "\" goto=\"" + textBlocks.indexOf(linkedTB) + "\" condition=\"" + linkCondition +"\"/>\n";            
		}
        if (ctb.flagChange) {
            result += "    <flagChange charId=\"" + ctb.flagCharId + "\" Id=\"" + ctb.flagId + "\" value=\"" + ctb.flagVal +  "\"/>\n";
        }
        if (ctb.itemTransfer) {
            result += "    <itemTransfer to=\"" + ctb.giveTo + "\" itemName=\"" + ctb.itemName + "\" count=\"" + ctb.itemsCount +  "\" canGiveEng=\"" + ctb.canGiveSpeech[0] +  "\" canGiveRus=\"" + ctb.canGiveSpeech[1]  +  "\" cantGiveEng=\"" + ctb.cantGiveSpeech[0]  +  "\" cantGiveRus=\"" + ctb.cantGiveSpeech[1]+ "\"/>\n";
        }
	    result += "  </speechBlock>\n";
    }
	result += "</dialog>\n";
    
    for (var i = 0; i < textBlocks.length; ++i) {
        textBlocks[i].parent = textBlocks.indexOf(textBlocks[i].parent);
        for (var j = 0; j < textBlocks[i].links.length; ++j) {
            textBlocks[i].linkedTextBlocks[j] = textBlocks.indexOf(textBlocks[i].linkedTextBlocks[j]);
        }
    }
    var cache = [];
    var jsonStr = JSON.stringify(textBlocks, function(key, value) {
        if (typeof value === 'object' && value !== null) {
            if (cache.indexOf(value) !== -1) {
                // Circular reference found, discard key
                return;
            }
            // Store value in our collection
            cache.push(value);
        }
        return value;
    });
    cache = null;
    for (var i = 0; i < textBlocks.length; ++i) {
        if (textBlocks[i].parent == -1) {
            textBlocks[i].parent = null;
        } else {
            textBlocks[i].parent = textBlocks[textBlocks[i].parent];
        }
        for (var j = 0; j < textBlocks[i].links.length; ++j) {
            textBlocks[i].linkedTextBlocks[j] = textBlocks[textBlocks[i].linkedTextBlocks[j]];
        }
    }
     
    result += "<!--<editor>" + jsonStr + "</editor>-->";
	var field = document.getElementById("copyField");
	field.value = result;
	field.select();
    document.execCommand("Copy");
	alert("XML copied!");
	field.value = "";
}

function handleLoadFile(e) {
    e.stopPropagation();
    e.preventDefault();
    
    var f = e.target.files[0]; 

    if (f) {
      var r = new FileReader();
      r.onload = function(e) { 
	      var contents = e.target.result;
            
        var editorTag = contents.match(/<editor>(.*)<\/editor>/g)[0];
        var jsonStr = editorTag.substr(8, editorTag.length - 17);
        
        textBlocks = JSON.parse(jsonStr);
        for (var i = 0; i < textBlocks.length; ++i) {
            newTB = createTextBlock(false, true);
            
            newTB.x = textBlocks[i].x;
            newTB.y = textBlocks[i].y;
            newTB.lines = textBlocks[i].lines;
            newTB.spriteName = textBlocks[i].spriteName;
            newTB.update(language);
            newTB.text = textBlocks[i].text;
            newTB.parent = textBlocks[i].parent;
            newTB.links = textBlocks[i].links;
            newTB.linkedTextBlocks = textBlocks[i].linkedTextBlocks;
            newTB.linksPriorities = textBlocks[i].linksPriorities;
            newTB.linksConditions = textBlocks[i].linksConditions;
            newTB.speakerName = textBlocks[i].speakerName;
            newTB.linkRemove = -1;
            newTB.isStart = textBlocks[i].isStart;
            newTB.flagChange = textBlocks[i].flagChange;
            newTB.flagCharId = textBlocks[i].flagCharId;
            newTB.flagId = textBlocks[i].flagId;
            newTB.flagVal = textBlocks[i].flagVal;
            newTB.itemTransfer = textBlocks[i].itemTransfer;
            newTB.giveTo = textBlocks[i].giveTo;
            newTB.itemName = textBlocks[i].itemName;
            newTB.itemsCount = textBlocks[i].itemsCount;
            newTB.canGiveSpeech = textBlocks[i].canGiveSpeech;
            newTB.cantGiveSpeech = textBlocks[i].cantGiveSpeech;
            
            textBlocks[i] = newTB;
        }
        for (var i = 0; i < textBlocks.length; ++i) {
            if (textBlocks[i].parent == -1) {
                textBlocks[i].parent = null;
            } else {
                textBlocks[i].parent = textBlocks[textBlocks[i].parent];
            }
            for (var j = 0; j < textBlocks[i].links.length; ++j) {
                textBlocks[i].linkedTextBlocks[j] = textBlocks[textBlocks[i].linkedTextBlocks[j]];
            }
        }
      };
      r.readAsText(f);
    } else { 
      alert("Failed to load file");
    }  
}

var c = document.getElementById("myCanvas");
var ctx = c.getContext("2d");
var blockId = -1;
var removeId = -1;
c.onmousemove = function(e) { 
    blockId = -1;
    removeId = -1;
  for (var i = 0; i < textBlocks.length; ++i) {
      var curblock = textBlocks[i];
      var id = curblock.checkLinks(globalOffsetX, globalOffsetY, e.x - parseFloat(c.offsetLeft), e.y - parseFloat(c.offsetTop));
      if (id != -1) {
         removeId = id;
         blockId = i;
      }
    }
}
c.onmousedown = function(e) { 
  if (removeId != -1) {
    textBlocks[blockId].linkedTextBlocks[removeId].parent = null;
    textBlocks[blockId].linkedTextBlocks.splice(removeId, 1);
    textBlocks[blockId].links.splice(removeId, 1);
    textBlocks[blockId].linksPriorities.splice(removeId, 1);
    textBlocks[blockId].linksConditions.splice(removeId, 1);
  }
  if (currentEditingBlock != null) return;
  lastMouseX = e.x - parseFloat(c.offsetLeft);
  lastMouseY = e.y - parseFloat(c.offsetTop);
  var block = null;
  var offX, offY;
  for (var i = 0; i < textBlocks.length; ++i) {
        var curblock = textBlocks[i];
        if (curblock.x < (e.x - globalOffsetX) - parseFloat(c.offsetLeft) && curblock.y < (e.y - globalOffsetY) - parseFloat(c.offsetTop) && curblock.x + curblock.w > (e.x - globalOffsetX) - parseFloat(c.offsetLeft) && curblock.y + curblock.h > (e.y - globalOffsetY) - parseFloat(c.offsetTop)) {
            block = curblock;
            offX = (e.x - globalOffsetX) - block.x;
            offY = (e.y - globalOffsetY) - block.y;
            //break;
        }
  }
  if (block && !block.isStart) {
      document.onmousemove = function(e) {
        block.x = (e.x - globalOffsetX) - offX;
        block.y = (e.y - globalOffsetY) - offY;
      }

      c.onmouseup = function() {
        document.onmousemove = null;
        c.onmouseup = null;
      }
  } else {
    var text = [document.getElementById('linkText0').value, document.getElementById('linkText1').value];
    var priority = parseInt($('#linkPriority').val());
    var condition = document.getElementById('condition').value;
    
    for (var i = 0; i < textBlocks.length; ++i) {
        var curblock = textBlocks[i];
        var dist = Math.sqrt((curblock.x + curblock.w/2 - (e.x - globalOffsetX) + parseFloat(c.offsetLeft))*(curblock.x + curblock.w/2 - (e.x - globalOffsetX) + parseFloat(c.offsetLeft)) + (curblock.y + curblock.h - (e.y - globalOffsetY) + parseFloat(c.offsetTop))*(curblock.y + curblock.h - (e.y - globalOffsetY) + parseFloat(c.offsetTop)));
        if (dist < 10) {
            block = curblock;
            break;
        }
    }
    if (block) {
        block.links.push(text);
        block.linkedTextBlocks.push(null);
        block.linksPriorities.push(priority);
        if ($('#conditionalLink').prop('checked')) {
            block.linksConditions.push(condition);
        } else {
            block.linksConditions.push("");
        }
          lastMouseX = (e.x - globalOffsetX) - parseFloat(c.offsetLeft);
          lastMouseY = (e.y - globalOffsetY) - parseFloat(c.offsetTop);
        document.onmousemove = function(e) {
          lastMouseX = (e.x - globalOffsetX) - parseFloat(c.offsetLeft);
          lastMouseY = (e.y - globalOffsetY) - parseFloat(c.offsetTop);
        }
        c.onmouseup = function() {
			var found = false;
			var closestBlock;
            for (var i = 0; i < textBlocks.length; ++i) {
                if (textBlocks[i].isStart) continue;
				var curblock = textBlocks[i];
			    var dist = Math.sqrt((curblock.x + curblock.w/2 - lastMouseX)*(curblock.x + curblock.w/2 - lastMouseX) + (curblock.y + curblock.h/2 - lastMouseY)*(curblock.y + curblock.h/2 - lastMouseY));
                if (dist < 50 && curblock != block) {
					closestBlock = textBlocks[i];
					found = true;
					block.linkedTextBlocks[block.linkedTextBlocks.length-1] = closestBlock;
                    closestBlock.parent = block;
					break;
				}
            }
			if (found) {
				for (var i = 0; i < block.linkedTextBlocks.length-1; ++i) {
					if (block.linkedTextBlocks[i] == closestBlock) {
						found = false;
						break;
					}
				}
			}
            document.onmousemove = null;
            c.onmouseup = null;
			if (!found || block == closestBlock) {
				block.linkedTextBlocks.pop();
				block.links.pop();
				block.linksPriorities.pop();
				block.linksConditions.pop();
			}
        }
    } else {
		for (var i = 0; i < textBlocks.length; ++i) {
			var curblock = textBlocks[i];
			var dist = Math.sqrt((curblock.x + curblock.w + 10 - (e.x - globalOffsetX) + parseFloat(c.offsetLeft))*(curblock.x + curblock.w + 10 - (e.x - globalOffsetX) + parseFloat(c.offsetLeft)) + (curblock.y - 10 - (e.y - globalOffsetY) + parseFloat(c.offsetTop))*(curblock.y - 10 - (e.y - globalOffsetY) + parseFloat(c.offsetTop)));
			if (dist < 5) {
				block = curblock;
				break;
			}
		}
		if (block && !block.isStart) {
			for (var i = 0; i < textBlocks.length; ++i) {
				if (textBlocks[i].parent == block) {
					textBlocks[i].parent = null;
				}
				for (var j = 0; j < textBlocks[i].linkedTextBlocks.length; ++j) {
					if (textBlocks[i].linkedTextBlocks[j] == block) {
						textBlocks[i].linkedTextBlocks.splice(j, 1);
						textBlocks[i].links.splice(j, 1);
						textBlocks[i].linksPriorities.splice(j, 1);
						textBlocks[i].linksConditions.splice(j, 1);
					}
				}
			}
			textBlocks.splice(textBlocks.indexOf(block), 1);
		} else {
            for (var i = 0; i < textBlocks.length; ++i) {
                var curblock = textBlocks[i];
                var dist = Math.sqrt((curblock.x + curblock.w + 10 - (e.x - globalOffsetX) + parseFloat(c.offsetLeft))*(curblock.x + curblock.w + 10 - (e.x - globalOffsetX) + parseFloat(c.offsetLeft)) + (curblock.y - (e.y - globalOffsetY) + parseFloat(c.offsetTop))*(curblock.y - (e.y - globalOffsetY) + parseFloat(c.offsetTop)));
                if (dist < 5) {
                    block = curblock;
                    break;
                }
            }
            if (block && !block.isStart) {
                textBlocks.push($.extend(true, Object.create(Object.getPrototypeOf(block)), block));
                textBlocks[textBlocks.length - 1].parent = null;
                textBlocks[textBlocks.length - 1].links = [];
                textBlocks[textBlocks.length - 1].linkedTextBlocks = [];
                textBlocks[textBlocks.length - 1].linksPriorities = [];
                textBlocks[textBlocks.length - 1].linksConditions = [];
            } else {
                for (var i = 0; i < textBlocks.length; ++i) {
                    var curblock = textBlocks[i];
                    var dist = Math.sqrt((curblock.x + curblock.w + 10 - (e.x - globalOffsetX) + parseFloat(c.offsetLeft))*(curblock.x + curblock.w + 10 - (e.x - globalOffsetX) + parseFloat(c.offsetLeft)) + (curblock.y + 10 - (e.y - globalOffsetY) + parseFloat(c.offsetTop))*(curblock.y + 10 - (e.y - globalOffsetY) + parseFloat(c.offsetTop)));
                    if (dist < 5) {
                        block = curblock;
                        break;
                    }
                }
                if (block && !block.isStart) {
                    $('#blockParams').css('background-color', '#aaaaaa');
                    $('#createButton').html('Update');
                    currentEditingBlock = block;
                    $('#text0').html(block.text[0]);
                    $('#text1').html(block.text[1]);
                    $('#textEx0').html(block.speakerName[0]);
                    $('#textEx1').html(block.speakerName[1]);
                    $('#createButton').unbind();
                    $('#createButton').click(function(){
                        $('#createButton').html('Create');
                        $('#blockParams').css('background-color', '#ffffff');
                        $('#createButton').unbind();
                        $('#createButton').click(function(){createTextBlock(false, false);});
                        var newTB = createTextBlock(false, true);
                        newTB.x = currentEditingBlock.x;
                        newTB.y = currentEditingBlock.y;
                        newTB.parent = currentEditingBlock.parent;
                        for (var i = 0; i < textBlocks.length; ++i) {
                            var id = textBlocks[i].linkedTextBlocks.indexOf(currentEditingBlock);
                            if (id != -1) {
                                textBlocks[i].linkedTextBlocks[id] = newTB;
                                newTB.parent = textBlocks[i];
                            }
                        }
                        newTB.links = currentEditingBlock.links;
                        newTB.linkedTextBlocks = currentEditingBlock.linkedTextBlocks;
                        newTB.linksPriorities = currentEditingBlock.linksPriorities;
                        newTB.linksConditions = currentEditingBlock.linksConditions;
                        textBlocks[textBlocks.indexOf(currentEditingBlock)] = newTB;
                        currentEditingBlock = null;
                    });
                    var txt0 = "";
                    for (var i = 0; i < block.lines[0].length; ++i) txt0 += block.lines[0][i];
                    var txt1 = "";
                    for (var i = 0; i < block.lines[1].length; ++i) txt1 += block.lines[1][i];
                    $('#text0').val(txt0);
                    $('#text1').val(txt1);
                    $('#textEx0').val(block.speakerName[0]);
                    $('#textEx1').val(block.speakerName[1]);
                    $('#imgName').val(block.spriteName);
                    tryImage(block.spriteName)
                    if (block.flagChange) {
                        $('#flagChange').prop('checked', true);
                        $('#flagOwnerId').prop( "disabled", false);
                        $('#flagId').prop( "disabled", false);
                        $('#flagTrue').prop( "disabled", false);
                        $('#flagFalse').prop( "disabled", false);
                        $('#flagOwnerId').val(block.flagCharId);
                        $('#flagId').val(block.flagId);
                        if (block.flagVal) {
                            $('#flagTrue').prop('checked');
                        } else {
                            $('#flagFalse').prop('checked');
                        }
                    } else {
                        $('#flagChange').prop('checked', false); 
                        $('#flagOwnerId').prop( "disabled", true);
                        $('#flagId').prop( "disabled", true);
                        $('#flagTrue').prop( "disabled", true);
                        $('#flagFalse').prop( "disabled", true);
                    }
                    if (block.itemTransfer) {
                        $('#itemTransfer').prop('checked', true);
                        $('#giveTo').prop( "disabled", false);
                        $('#itemName').prop( "disabled", false);
                        $('#itemCount').prop( "disabled", false);
                        $('#giveText0').prop( "disabled", false);
                        $('#notGiveText0').prop( "disabled", false);
                        $('#giveText1').prop( "disabled", false);
                        $('#notGiveText1').prop( "disabled", false);
                        $('#giveTo').val(block.giveTo);
                        $('#itemName').val(block.itemName);
                        $('#itemCount').val(block.itemsCount);
                        $('#giveText0').val(block.canGiveSpeech[0]);
                        $('#giveText1').val(block.canGiveSpeech[1]);
                        $('#notGiveText0').val(block.cantGiveSpeech[0]);
                        $('#notGiveText1').val(block.cantGiveSpeech[1]);
                    } else {
                        $('#itemTransfer').prop('checked', false);
                        $('#giveTo').prop( "disabled", true);
                        $('#itemName').prop( "disabled", true);
                        $('#itemCount').prop( "disabled", true);
                        $('#giveText0').prop( "disabled", true);
                        $('#notGiveText0').prop( "disabled", true);
                        $('#giveText1').prop( "disabled", true);
                        $('#notGiveText1').prop( "disabled", true);
                    }
                }
            }
        }
    }
	}
    if (!block) {
        var preX = globalOffsetX;
        var preY = globalOffsetY;
        globalOffsetStartX = e.x;
        globalOffsetStartY = e.y;
        
        document.onmousemove = function(e) {
            globalOffsetX = preX + e.x - globalOffsetStartX;
            globalOffsetY = preY + e.y - globalOffsetStartY;
        }

        c.onmouseup = function() {
            document.onmousemove = null;
            c.onmouseup = null;
        }        
    }
}

function tryImage(name) {
    name += ".png";
    for (var i = 0; i < sprites.length; ++i) {
        if (sprites[i].name == name) {
            var reader = new FileReader();

            reader.onload = function (e) {
                var img = $('<img>').attr('src', e.target.result);
                $('#imgPart').html(img);
            };

            reader.readAsDataURL(sprites[i]);
            //$('#imgPart').css('background-image', sprites[i]);
        }
    }
}

var lastMouseX = 0;
var lastMouseY = 0;
var language = 0;
var globalOffsetStartX = 0;
var globalOffsetStartY = 0;
var globalOffsetX = 0;
var globalOffsetY = 0;
var EDITOR_TEXT_FONT = "8pt Palatino Linotype";
var EDITOR_FONT = "10px Consolas";
var GAME_FONT = "24pt Palatino Linotype";
var MAX_LINE_WIDTH = 840;
var MAX_LINES = 4;
var currentEditingBlock = null;
var sprites = [];
ctx.font = EDITOR_FONT;
textBlocks = [];
window.setInterval(draw, 10);
function selectFolder(e) {
    sprites = e.target.files;
}
$('#langChoice0').click(function(){language = 0});
$('#langChoice1').click(function(){language = 1});
$('#createButton').click(function(){createTextBlock(false, false);});