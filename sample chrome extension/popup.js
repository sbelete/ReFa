function report(val) {
	chrome.tabs.query({active: true}, function(tabs) {
		let currTab = tabs[0];  
		chrome.tabs.sendMessage(currTab.id, {message: "report", arg: val}); 
	}); 	
}

function whitelistSite() {
	chrome.tabs.query({active: true}, function(tabs) {
		let currTab = tabs[0];  
		chrome.tabs.sendMessage(currTab.id, {message: "whitelistSite"}); 
	});
}

function whitelistPage() {
	chrome.tabs.query({active: true}, function(tabs) {
		let currTab = tabs[0];  
		chrome.tabs.sendMessage(currTab.id, {message: "whitelistPage"}); 
	});
}

function viewWhitelist() {
		chrome.tabs.query({active: true}, function(tabs) {
		let currTab = tabs[0];  
		chrome.runtime.sendMessage({message: "viewWhitelist"}); 
	});
}

function changeHighlightPars() {
	console.log('reached'); 
	var highlightParsCheckbox = document.getElementById('highlightPars');
	let status = highlightParsCheckbox.checked; 
	chrome.storage.local.set({highlightPars: status}, function(items){}); 
}

function changeHighlightLinks() {
	var highlightLinksCheckbox = document.getElementById('highlightLinks');
	let status = highlightLinksCheckbox.checked; 
	chrome.storage.local.set({highlightLinks: status}, function(items){}); 
}
let trueColor = "#b7cebd";
let falseColor =  "#ed8787"; 
document.addEventListener('DOMContentLoaded', function() {
	chrome.storage.local.get('trueTabs', function(items) {
		chrome.tabs.query({active: true}, function(tabs) {
			let isTrue; 
			console.log(items.trueTabs); 
			console.log('tab_'+tabs[0].id.toString()); 
			if (('tab_'+tabs[0].id.toString()) in items.trueTabs){
				isTrue = items.trueTabs['tab_'+tabs[0].id]; 
			} else {
				isTrue = true; 
			}
			if (!isTrue) {
				document.body.style.background = falseColor; 
			} else {
				document.body.style.background = trueColor; 
			}
		})
	})

	var reportFalse = document.getElementById('reportFalse'); 
	reportFalse.addEventListener('click', function() {
		report(false); 
	})

	var reportTrue = document.getElementById('reportTrue'); 
	reportTrue.addEventListener('click', function() {
		report(true); 
	})	

	var whitelistSiteButton = document.getElementById('addSiteWhitelist');
	whitelistSiteButton.addEventListener('click', function() {
		whitelistSite(); 
	})

	var whitelistPageButton = document.getElementById('addPageWhitelist'); 
	whitelistPageButton.addEventListener('click', function() {
		whitelistPage(); 
	})

	var viewWhitelistButton = document.getElementById('viewWhitelist'); 
	viewWhitelistButton.addEventListener('click', function() {
		viewWhitelist(); 
	})

	var highlightParsCheckbox = document.getElementById('highlightPars');
	chrome.storage.local.get('highlightPars', function(items){
		let highlight = items.highlightPars;
		highlightParsCheckbox.checked = highlight; 
		highlightParsCheckbox.addEventListener('click', function() {
			changeHighlightPars(); 
		}) 
	})

	var highlightLinksCheckbox = document.getElementById('highlightLinks');
	chrome.storage.local.get('highlightLinks', function(items){
		let highlight = items.highlightLinks;
		highlightLinksCheckbox.checked = highlight; 
		highlightLinksCheckbox.addEventListener('click', function() {
			changeHighlightLinks(); 
		})
	})
}); 