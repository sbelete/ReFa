// content.js
let check = true;
let url = document.URL;
//import Chart from "chrome-extension://lmijopjkjilemgimgkoihfklhmkiephf/Chart.js";
//$.getScript("chrome-extension://lmijopjkjilemgimgkoihfklhmkiephf/Chart.js", function(){

   //alert("Script loaded but not necessarily executed.");

//});

// setting up page whitelist, with three default sites
let whitelist = ["google.com", "stackoverflow.com", "gmail.com"];
chrome.storage.local.get({'whitelistSites': whitelist, 'whitelistPages': []}, function(items) {
  let currWhitelist = items.whitelistSites;
  if (currWhitelist.length == 3) {
    chrome.storage.local.set({'whitelistSites': currWhitelist}, function(items){
    })
  }
  let currWhitelistpages = items.whitelistPages;
  if (currWhitelistpages.length == 0) {
    chrome.storage.local.set({'whitelistPages': []}, function(items){
    })
  }
  if ( window.location.pathname == '/' ){
    check = false;
  } else if($.inArray((new URL(url)).hostname, currWhitelist) > -1){
    check = false;
    // //var myRadarChart = new Chart(ctx).Radar(radarData, radarOptions);
    // var myRadarChart = new Chart(ctx, {
    // type: 'radar',
    // data: radarData})
  } else if ($.inArray(url, currWhitelistpages) > -1) {
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
      chrome.storage.local.set({'whitelistSites':currWhitelist}, function(items) {
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
  let wordpress = (docHTML.match(/wordpress/g)||[]).length;
  if (wordpress >= 1) {
    wordpress = 0; 
  } else {
    wordpress = 1; 
  }
  const postParameters = {categories: categories,baseURL: baseURL, isFalse: val, scores: articleScores, c1score: c1score, wordpress: wordpress};
  http.send(JSON.stringify(postParameters));
}

class Graph {
  constructor(category, scoreCategories, scores, avgScores, container) {
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
    this.container.html("<canvas id=\"fakeNewsGraph\">");
    let canvas = $("#fakeNewsGraph")[0];
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
          label: 'Emotion scores for this article',
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
          text: 'Warning: This article could contain false or sensationalized content.',
          fontColor: 'red'
     }
    }
    });
  }

}

function articleHandler(alop){
    const postParameters = {article: alop};
    const http = new XMLHttpRequest();
    const path = "https://localhost:4567/article";
    http.open("POST", path, true);
    http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    // Set to global variables
    http.onload = function () {
        // do something to response
        const responseJSON = JSON.parse(this.responseText);
        console.log(responseJSON);
        const valid = responseJSON['valid'];
        const scores = responseJSON['scores'];
        avgScores = responseJSON['avg'];
        const category = responseJSON['category'];
        let scoreCategories = ['sentiment', 'anger', 'joy', 'sadness',
        'disgust', 'fear'];
        articleValid = valid;
        articleScores = scores;
        c1score = responseJSON['c1score'];
        if (valid > 0) {
          fakeHandler(category, scoreCategories, scores, avgScores); 
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
    let summaryGraph = new Graph(category, scoreCategories, scores, avgScores, container);
    let alertPath = chrome.extension.getURL('alert.png');
    $("h1").html($("h1").html() + "<img id=\'fakeAlertImage\'src=\'" + alertPath + "\'>");
    // Setting up fake alert image
    $("#fakeAlertImage")[0].style.width = '9%'; 
    $("#fakeAlertImage")[0].style.height = 'auto'; 
    $("#fakeAlertImage").click(function() {
      summaryGraph.toggleShow();
    })  
}

function paragraphHandler(alop, positions){
    const postParameters = {paragraphs: alop};
    /// TODO: catch the negative paragraph truthiness score
    const http = new XMLHttpRequest();
    const path = "https://localhost:4567/paragraph";
    http.open("POST", path, true);
    http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    http.onload = function () {
      // do something to response
      const responseJSON = JSON.parse(this.responseText);
      const valid = responseJSON['valid'];
      const scores = responseJSON['scores'];

      let queue = new PriorityQueue();
      for(let i = 0; i < scores.length; i++){
        if(articleValid > .5){
          queue.push({p : i}, dot(scores[i], avgScores, articleScores));
        }
      }
      for(let i = 0; i < Math.floor(scores.length*articleValid/4.0); i++){
        $("p").eq(positions[queue.pop().p]).css("background-color", "yellow");
        $("p").eq(positions[queue.pop().p]).tooltip(); 
        $("p").eq(positions[queue.pop().p]).prop('title', 'yo'); 
      }
      $( document ).tooltip()
    };

    // console.log("Paragraph Post JSON");
    // console.log(postParameters);
    http.send(JSON.stringify(postParameters));
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
