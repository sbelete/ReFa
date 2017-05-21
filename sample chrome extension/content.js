// content.js
let check = true;
let url = document.URL;
//import Chart from "chrome-extension://lmijopjkjilemgimgkoihfklhmkiephf/Chart.js";
//$.getScript("chrome-extension://lmijopjkjilemgimgkoihfklhmkiephf/Chart.js", function(){

   //alert("Script loaded but not necessarily executed.");

//});
//TODO: 
//-Allow fine-tuning of display parameters : 
// -cutoff score
// -cutoff for paragraphs
// -whether or not to highlight paragrphs
//-paragraph hover display thing
// setting up page whitelist, with three default sites
let highlightPars;
let highlightLinks; 
let validScore = 0.2; 
let defaultWhitelist = ["google.com", "stackoverflow.com", "gmail.com"];
chrome.storage.local.get(null, function(items) {
  let currWhitelist; 
  if (items.hasOwnProperty('whitelistSites')){
    currWhitelist = items.whitelistSites;
  } else {
    currWhitelist = defaultWhitelist
  } 
  let currWhitelistpages;
  if (!items.hasOwnProperty('whitelistPages')) {
    currWhitelistPages = []
  } else {
    currWhitelistPages = items.whitelistPages; 
  }
  if (!items.hasOwnProperty('highlightPars')) {
    highlightPars = true;  
  } else {
    highlightPars = items.highlightPars;
  }
  if (!items.hasOwnProperty('highlightLinks')) {
    highlightLinks = true; 
  } else {
    highlightLinks = items.highlightLinks;
  }

  chrome.storage.local.set({whitelistPages: currWhitelistpages, whitelistSites: currWhitelist,
    highlightPars: highlightPars, highlightLinks: highlightLinks, validScore: validScore});
 if ( window.location.pathname == '/' ){
    check = false;
  } else if($.inArray((new URL(url)).hostname, currWhitelist) > -1) {
    check = false;
    // //var myRadarChart = new Chart(ctx).Radar(radarData, radarOptions);
    // var myRadarChart = new Chart(ctx, {
    // type: 'radar',
    // data: radarData})
  } else if (currWhitelistPages.includes((new URL(url)).href.toString())) {
    check = false;
  }

  if (check) {
    createGraph();
  }
});

let articleValid;
let articleScores;
let avgScores;
let c1score;
let articleParagraphs;
let articlePositions;
let category; 
scoreCategories = ['sentiment', 'anger', 'joy', 'sadness','disgust', 'fear'];
function createGraph() {
  // setting up per-paragraph post request
  articleParagraphs = [];
  articlePositions = [];
  let temp;
  let length;
  let article = [];
  // iterating through all of the paragraph elements in the document
  for (let i = 0; i < $("p").length; i++) {
      temp = $("p")[i].innerText
      length = temp.split(" ").length;
      if(length > 7) {
        article.push(temp);
        if(length > 25){
        articleParagraphs.push(temp);
        articlePositions.push(i);
        }
      }
  }


  articleHandler(article);
  linkHandler();
}

// event listener from the popup
chrome.runtime.onMessage.addListener(
  function(request, sender, sendResponse) {
  if( request.message === "report" ) {
      let val = request.arg;
    if (check) {
      if (val) {
        reportPage(true);
      } else {
        reportPage(false);
      }
    } else {
      alert('You are trying to report a page in your whitelist or a homepage of a website');
    }
  }
  if(request.message === "whitelistSite") {
      let url = document.URL;
      let baseURL = new URL(url).hostname;
      updateWhitelistSites(baseURL);
    }
  if(request.message === "whitelistPage") {
    let url = document.URL;
    updateWhitelistPages(url);
  }
}
);

function updateWhitelistSites(url) {
  let currWhitelist;
  chrome.storage.local.get(null, function(items) {
    currWhitelist = items.whitelistSites;
    if ($.inArray(url, currWhitelist) > -1) {
      alert('the site ' + url + ' was already in your whitelist!');
    } else {
      currWhitelist.push(url);
      chrome.storage.local.set({'whitelistSites':currWhitelist}, function(items) {
      alert('the site ' + url + ' has been added to your whitelist successfully!');
    })
  }
  })
}

function updateWhitelistPages(url) {
  let currWhitelist;
  chrome.storage.local.get(null, function(items) {
    currWhitelist = items.whitelistPages;
    if ($.inArray(url, currWhitelist) > -1) {
      alert('the webpage: ' + url + ' was already in your whitelist!');
    } else {
      currWhitelist.push(url);
      chrome.storage.local.set({'whitelistPages':currWhitelist}, function(items) {
      alert('the webpage: ' + url + ' has been added to your whitelist successfully!');
    })
  }
  })
}

// report the current page as fake news, or list it as good
function reportPage(val) {
  let url = document.URL;
  let urlArr = url.split('?');
  let baseURL = urlArr[0];
  const http = new XMLHttpRequest();
  const path = "https://localhost:4567/review";
  http.open("POST", path, true);
  http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  let docHTML = $("html").html();
  let wordpress = (docHTML.match(/wp-content/g)||[]).length;
  if (wordpress >0) {
    wordpress = 1; 
  }
  const postParameters = {categories: scoreCategories,baseURL: baseURL, isFalse: val, scores: articleScores, c1score: c1score, wordpress: wordpress};
  http.send(JSON.stringify(postParameters));
  alert('This article has been reported');
}

class Graph {
  constructor(category, scoreCategories, scores, avgScores, container, graphId, graphTitle, graphLabel) {
    this.show = false;
    let canvHeight = 150;
    // Set up canvas
    this.container = container;
    this.font = 'Arial'
    // Store data variables
    this.scoreCategories = scoreCategories;
    this.scores = scores;
    this.avgScores = avgScores;
    this.category = category;
    // Store the graph constants
    this.drawn = false;
    this.radarChart;
    this.graphId = graphId; 
    this.graphTitle = graphTitle; 
    this.graphLabel = graphLabel; 
  }

  toggleShow() {
    if(this.show) {
      this.container.html("");
      this.show = false;
    } else {
      this.draw();
      this.show = true;
    }
  }

  draw() {
    this.container.html("<canvas id=\""+this.graphId+"\">");
    let canvas = $("#"+this.graphId)[0];
    canvas.height = 150;
    // Radar Data
    // this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)
    var radarData = {
     labels : this.scoreCategories,
     datasets : [
       {
         fillColor : "rgba(150,51,51,0.5)",
          strokeColor : "rgba(150,51,51,1)",
          data : this.scores,
          label: this.graphLabel,
          backgroundColor : "rgba(150,51,51,0.5)"
       },
       {
          fillColor : "rgba(151,187,205,0.5)",
         strokeColor : "rgba(151,187,205,1)",
         data : this.avgScores,

         label: 'Average scores for articles in the category ' + this.category,
         backgroundColor: "rgba(51,150,70,0.5)"

        }
      ],
    }
    let ctx = canvas.getContext('2d');
    //creating Chart
    this.radarChart = new Chart(ctx, {
    type: 'radar',
    data: radarData,
    options: {
      title: {
          display: true,
          text: this.graphTitle,
          fontColor: 'red'
     }
    }
    });
  }
}

function articleHandler(alop){
    let docHTML = $("html").html();
    let wordpress = (docHTML.match(/wordpress/g)||[]).length;
    const postParameters = {article: alop, wp : wordpress};
    const http = new XMLHttpRequest();
    const path = "https://localhost:4567/article";
    http.open("POST", path, true);
    http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    // Set to global variables
    http.onload = function () {
        // do something to response
        const responseJSON = JSON.parse(this.responseText);
        validLink = responseJSON['valid'];
        console.log(responseJSON);
        const valid = responseJSON.valid;
        console.log("VALID: " + valid); 
        articleScores = responseJSON['scores'];
        avgScores = responseJSON['avg'];
        category = responseJSON['categories'][0];
        articleValid = validLink; 
        const c1score = responseJSON['c1score'];
        
        if (valid < validScore) {
       
          fakeHandler(category, scoreCategories, articleScores, avgScores);
        }
        paragraphHandler(articleParagraphs, articlePositions);
    };
    http.send(JSON.stringify(postParameters));
}

function fakeHandler(category, scoreCategories, scores, avgScores) {
    let badIconPath = chrome.extension.getURL('badIcon.png');
    chrome.runtime.sendMessage({message: 'setIcon', iconpath: badIconPath});
    $("h1").eq(0).after("<div id=\"fakeNewsGraphContainer\"></div>");

    let container = $("#fakeNewsGraphContainer");
    let graphId = "fakeNewsGraph"
    let graphTitle = 'Warning: This article could contain false or sensationalized content.'; 
    let graphLabel = 'Average emotion scores for non-sensationalized articles in this category'
    let summaryGraph = new Graph(category, scoreCategories, scores, avgScores, container
      ,graphId, graphTitle, graphLabel);
    let alertPath = chrome.extension.getURL('alert.png');
    console.log('heading'); 
    $("h1").eq(0).html($("h1").html() + "<img style=\'width:\'60px; height:60px' id=\'fakeAlertImage\'src=\'" + alertPath + "\'>");
    // Setting up fake alert image
    $("#fakeAlertImage")[0].style.width = '60px';
    $("#fakeAlertImage")[0].style.height = 'auto';
    $("#fakeAlertImage").click(function() {
      summaryGraph.toggleShow();
    })
}

function paragraphHandler(alop, positions){
    console.log(alop); 
    const postParameters = {paragraphs: alop};
    /// TODO: catch the negative paragraph truthiness score
    const http = new XMLHttpRequest();
    const path = "https://localhost:4567/paragraph";
    http.open("POST", path, true);
    http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    http.onload = function () {
      // do something to response
      const responseJSON = JSON.parse(this.responseText);
      console.log(responseJSON); 
      const valid = responseJSON['valid'];
      const scores = responseJSON['scores'];

      let queue = new PriorityQueue();
      for(let i = 0; i < scores.length; i++){
        if(articleValid < validScore){
        
          console.log(scores[i]); 
          console.log(avgScores); 
          console.log(articleScores); 
          queue.push({p : i, scores: scores[i]}, dot(scores[i], avgScores, articleScores));
        } else {
            //queue.push({p : i}, dot(scores[i], articleScores, avgScores));
        }
      }

      if (highlightPars){
      for(let i = 0; i < Math.min(queue.heap.length, Math.ceil(scores.length*((1-articleValid)/6.0))); i++){
        let currpos = queue.pop(); 
        $("p").eq(positions[currpos.p]).css("background-color", "#ff5050");
        $("p").eq(positions[currpos.p]).after("<div class='floating' id='fakeParagraph"+currpos.p.toString()+"'></div>");  
        let position = $("p").eq(positions[currpos.p]).position(); 
        let posLeft = position.left + 100; 
        let posTop = position.top + 100; 
        let container = $("#fakeParagraph"+currpos.p.toString()); 
        container.css({'position': 'absolute', 'left': posLeft.toString()
          , 'top': posTop.toString(), 'background-color': '#ffffff'
          , 'box-shadow': '0 0 15px #00214B',  'height' : 'auto', 'width': '70%'})
        let graphTitle = "Warning: This paragraph could be sensationalized"; 
        let graphLabel = "Emotion scores for this paragraph"
        let currGraph = new Graph(category, scoreCategories, currpos.scores, avgScores, container, "fakeGraph"+currpos.p.toString()
          , graphTitle, graphLabel); 
        $("p").eq(positions[currpos.p]).hover(function(event) {
          container.css({ 'border-style': 'solid', 'position' : 'relative'});  
          currGraph.toggleShow(); 
        }, function() {
          currGraph.toggleShow();
          container.css({ 'border-style': 'none'}); 
        }); 
      }
    }
    highlight_link();
    }
    // console.log("Paragraph Post JSON");
    // console.log(postParameters);
    http.send(JSON.stringify(postParameters));
}

let validLink = null;

function linkHandler(){
  const http = new XMLHttpRequest();
  const path = "https://localhost:4567/link";
  http.open("POST", path, true);
  http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

  http.onload = function () {
    // do something to response
    console.log(this); 
    const responseJSON = JSON.parse(this.responseText);
    validLink = responseJSON['valid'];
    highlight_link();
  };

  // $("p").find("a").css( "background-color", "red" );

  let articleLink = [];
  $("p").find("a").each(function( index ) {
    articleLink.push($(this).attr('href'));
    console.log( index + ": " + $( this ).attr('href'));
  });

  console.log(articleLink);
  const postParameters = {links: articleLink};
  http.send(JSON.stringify(postParameters));
}

function highlight_link(){
  if(validLink == null){
    return;
  }
  if (highlightLinks) {
  for(let i = 0; i < validLink.length; i++){
    console.log(validLink[i]);
    if(validLink[i] < validScore && validLink[i] > 0){
      $("p").find("a").eq(i).css("background-color", "#edeb8b"); // how to highlight
      $("p").find("a").eq(i).after('<div id=\'fakeLink'+i.toString()+'\'></div>');
      let position = $("p").find("a").eq(i).position(); 
      let posLeft = position.left + 200; 
      let posTop = position.top; 
      let container = $("#fakeLink"+i); 
      container.css({'color': 'red', position: 'relative', 'left': posLeft.toString()
        , 'top': posTop.toString(), 'background-color': '#ffffff'
        , 'box-shadow': '0 0 15px #00214B',  'height' : 'auto', 'width': '30%'})
      $("p").find("a").eq(i).hover(function() {
        console.log('hovering'); 
        container.html("This link refers to an article that is likely to have sensationalized content"); 
      }, function() {
        container.html(''); 
      })
    }
  }
}
}

function distance(score1, score2) {
  let sum = 0;
  for(let i = 0; i < score1.length; i++){
    sum += Math.pow(score1[i] + score2[i],2);
  }
  return Math.sqrt(sum);
}

function dot(parScore, avgScore, articleScore) {
  let diff1 = [];
  let diff2 = [];
  for (let i = 0; i < parScore.length; i++) {
    diff1.push(parScore[i] - avgScore[i]);
    diff2.push(articleScore[i] - avgScore[i]);
  }
  let sum = 0;
  for (let i = 0; i < diff1.length; i++) {
    sum += diff1[i] * diff2[i];
  }

  return sum;
}
