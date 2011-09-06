////////////////////////////////////////////////////////////////////////////
// Description: deal with the keyword suggestions when user input the keyword
// for query, it can provide the candidate keywords by queried frequencies from
// database
// Author:Jinhua Chen
// Date:26.6.2009
// Modified: 5.9.2011
///////////////////////////////////////////////////////////////////////////

var suggestions = new Array();
var consentTemplates = new Array();
var zz = -1;
var currentNum = -1;
var isie = (document.all) ? true:false; 
var keyStr = "";
var doKeyUp = false;
var suggestId = 'search_suggest';
var keywordId = 'templateName';
var suggestUrl;

function createSuggestDiv(searchElement, keywordElement) {
	
	if(document.getElementById(suggestId) != null) {
		return;
	}
	
	var divSearch = document.getElementById(searchElement);
	keywordId = keywordElement;
	var divKeyword = document.getElementById(keywordId);
	var divSuggest = document.createElement('div');
	divSuggest.id = suggestId;
	divSuggest.style.width = divKeyword.offsetWidth+ 'px';
	divSuggest.style.zIndex = '1000';
	divSuggest.style.position = 'absolute';
	divSuggest.style.top = divKeyword.offsetHeight + 'px';
	divSuggest.style.left = divKeyword.offsetLeft + 'px';
	divSuggest.style.left = '368px';
	divSuggest.style.display = 'none';
	
	divSearch.appendChild(divSuggest);
}

function beKeyDown(e)
{
  var keyValue = 0;
  if(isie)
    keyValue = event.keyCode;
  else
    keyValue = e.which;
  //alert(keyValue);
  if (keyValue == 38 || keyValue == 40)
  {
    if(suggestions.length >0)
      searchKey(keyValue);
  }else if(keyValue == 13){
    searchKey(keyValue);
  }
}

// add the delay to avoid the interruption when entering keywords
function beKeyUp(e)
{
  clearTimeout(doKeyUp);
  var keyValue = 0;
  if(isie)
    keyValue = event.keyCode;
  else
    keyValue = e.which;
    
  doKeyUp = setTimeout("getCandidateKeywords('" + keyValue + "')",300);
}

function getCandidateKeywords(keyValue) 
{                   
  if ((keyValue == 38 || keyValue == 40 || keyValue == 13))
  {
  //searchKey(keyValue);
  } else{
    currentNum = -1;
    zz = -1;
    suggestions = new Array();
    var key = document.getElementById(keywordId).value;
    keyStr = key;
    if(key.length>0)
    {
    	var url = suggestUrl;
		
		jQuery.ajax({
			  type: 'POST',
			  url: url,
			  global:false,
			  data: {'keyword':key},
			  success: function(data) {
				var obj = eval('(' + data + ')');
				var json = obj.response;
				consentTemplates = json["result"];
				
				for(var i=0; i < consentTemplates.length; i++) {
					suggestions[i] = consentTemplates[i]['otsikko'];
				}
				
				if(suggestions.length > 0) {
					var DivRef= document.getElementById(suggestId);
			        DivRef.style.border = "solid #777 1px";
			        document.getElementById(suggestId).innerHTML="";
			        for(var i=0;i <suggestions.length;i++)
			        {
			          if(suggestions[i]!="")
			          {
			            document.getElementById(suggestId).innerHTML+=" <div id='item" + i + "' class='itemBg' onmouseover='beMouseOver(" + i +")' onmouseout='beMouseOut(" + i + ")' onclick='beClick(" + i + ")'>" + suggestions[i] + " </div>";
			          }
			        }
			        document.getElementById(suggestId).innerHTML+=" <div class='item_button' onclick='hiddenDiv();'> <font color='#0000ff'>close </font> </div>";
			        document.getElementById(suggestId).style.display="inline";
				}else {
					document.getElementById(suggestId).style.display="none";
				}
			  }
			  
			});	
                    
    } else
    {
      document.getElementById(suggestId).style.display="none";
    }
  }
}  

function hiddenDiv() 
{ 
  document.getElementById(suggestId).style.display="none";
} 
              
function beMouseOverEFF(i) 
{ 
  if ((i>=0)&(i <=suggestions.length-1))
  {
    document.getElementById("item" + i).className="item_high";
  }
} 


function beMouseOutEFF(i) 
{ 
  if ((i>=0)&(i <=suggestions.length-1))
  {
    document.getElementById("item" + i).className="item_normal";
  }
} 

function beMouseOver(i) 
{ 
  document.getElementById(keywordId).focus();
  beMouseOutEFF(zz);
  currentNum=zz=i;
  beMouseOverEFF(zz);
} 

function beMouseOut(i) 
{ 
  beMouseOutEFF(i);
} 

function beClick(i) 
{ 
  currentNum = i;
  document.getElementById(keywordId).value=suggestions[i];
  document.getElementById(suggestId).style.display="none";
  document.getElementById(keywordId).focus();
} 
function searchKey(e)  
{
  if (e == 38){  // up key
    if(currentNum == -1){
      currentNum = suggestions.length -2;
      beMouseOver(currentNum);
      document.getElementById(keywordId).value=suggestions[currentNum];
    }else if(currentNum == 0){
      document.getElementById(keywordId).value=keyStr;
      document.getElementById("item" + currentNum).className="item_normal";
      currentNum = -1;
    }else{
      currentNum --;
      beMouseOver(currentNum);
      document.getElementById(keywordId).value=suggestions[currentNum];
    }
            		
  }else if(e == 40){ //down key
    if(currentNum == -1){
      currentNum = 0;
      beMouseOver(currentNum);
      document.getElementById(keywordId).value=suggestions[currentNum];
            		           		
    }else if(currentNum == suggestions.length -2){
      document.getElementById(keywordId).value=keyStr;
      document.getElementById("item" + currentNum).className="item_normal";
      currentNum = -1;
    }else{
      currentNum ++;
      beMouseOver(currentNum);
      document.getElementById(keywordId).value=suggestions[currentNum];
    }
            		 
  }else if(e == 13){ //enter key
    document.getElementById(suggestId).style.display="none";
    return false;          		 
  }
   
}