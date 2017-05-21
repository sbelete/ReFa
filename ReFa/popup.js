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

document.addEventListener('DOMContentLoaded', function() {
	var reportTrue = document.getElementById('reportTrue'); 
	reportTrue.addEventListener('click', function() {
		report(true); 
	})

	var reportFalse = document.getElementById('reportFalse'); 
	reportFalse.addEventListener('click', function() {
		report(false); 
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
}); 