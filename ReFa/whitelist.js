//Global sites, pages variables 
let sites; 
let pages; 

//Loading sites and pages
chrome.storage.local.get(null, function(items) {
	// Drawing table of existing sites
	sites = items.whitelistSites; 
	pages = items.whitelistPages;
	loadPage(); 
}); 

// Adding handlers for add-site and add-page
let submit_site = $("#submit_site") 
submit_site.click(function() {
	let url = document.getElementById("whitelisted_site_name").value; 
	adjustWhitelistSites(url); 
	loadPage(); 
})

let submit_page = $("#submit_page");
submit_page.click( function() {
	let url = document.getElementById("whitelisted_page_name").value; 
	adjustWhitelistPages(url); 
	loadPage(); 
})

function loadPage() {
	let siteHTML = ""; 
	let pagesHTML = "";
	for (let i = 0; i < sites.length; i++) {
		siteHTML += "<tr id='s"+i.toString()+"'><td>"+sites[i]+"</td><td><button type='button'"+
		"id='siteButtons"+i.toString()+"'>Remove</button></td></tr>"
	}
	for (let i = 0; i < pages.length; i++) {
		pagesHTML += "<tr id='p"+i.toString()+"'><td>"+pages[i]+"</td><td><button type='button'"+
		"id='pageButtonp"+i.toString()+"'>Remove</button></td></tr>"
	}
	let siteTable = $("#whitelisted_sites")
	let pageTable = $("#whitelisted_pages")
	siteTable.html(siteHTML); 
	pageTable.html(pagesHTML); 
	for (let i =0; i < sites.length; i++) {
		let currButton = document.getElementById("siteButtons"+i.toString()); 
		currButton.addEventListener('click', function() {
			adjustWhitelistSites("s"+i.toString()); 
		})
	}

	for (let i =0; i < pages.length; i++) {
		let currButton = document.getElementById("pageButtonp"+i.toString()); 
		currButton.addEventListener('click', function() {
			adjustWhitelistPages("p"+i.toString()); 
		})
	}
}

function adjustWhitelistSites(name) {
	let i = name.split('')[1]; 
	sites.splice(i,1); 
	chrome.storage.local.set({whitelistSites : sites}, function() {
		loadPage(); 
	}); 
}

function adjustWhitelistPages(name) {
	let pageName = $("#"+name+" td").get(0).innerText; 
	let i = name.split('')[1]; 
	pages.splice(i,1); 
	chrome.storage.local.set({whitelistPages : pages}, function() {
		loadPage(); 
	}); 
}