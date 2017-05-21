//Global sites, pages variables 
let sites = []; 
let pages = []; 

//Loading sites and pages
chrome.storage.local.get(null, function(items) {
	// Drawing table of existing sites
	sites = items.whitelistSites; 
	pages = items.whitelistPages; 
// 	if (pages) {
// 	pages = items.whitelistPages;
// }
	loadPage(); 
}); 
window.onload=function() {
// Adding handlers for add-site and add-page
let submit_site = document.getElementById("submit_site"); 
submit_site.addEventListener('click', function() {
	let url = document.getElementById("whitelisted_site_name").value; 
	addWhitelistSites(url); 
	loadPage(); 
})

let submit_page = document.getElementById("submit_page");
submit_page.addEventListener('click',  function() {
	let url = document.getElementById("whitelisted_page_name").value; 
	addWhitelistPages(url); 
	loadPage(); 
})
}

function loadPage() {
	let siteHTML = "";
	let pagesHTML = "";
	for (let i = 0; i < sites.length; i++) {
		siteHTML += "<li class='list-group-item justify-content-between' id='s"+i.toString()+"'>"+sites[i]+"<div class='pull-right'><div class='mx-auto'><button type='button'"+
		"id='siteButtons"+i.toString()+"' class='btn btn-sm' >Remove</button></div></div></li>"
	}
	
	for (let i = 0; i < pages.length; i++) {
		pagesHTML += "<li class='list-group-item justify-content-between' id='p"+i.toString()+"'>"+pages[i]+"<div class='pull-right'><button type='button'"+
		"id='pageButtonp"+i.toString()+"' class='btn btn-sm'>Remove</button><div></li>"
	}

	let siteTable = $("#whitelisted_sites")
	siteTable.html(siteHTML+pagesHTML);
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
	//let pageName = $("#"+name+" td").get(0).innerText; 
	let i = name.split('')[1]; 
	pages.splice(i,1); 
	chrome.storage.local.set({whitelistPages : pages}, function() {
		loadPage(); 
	}); 
}

function addWhitelistSites(name) {
	name = 'https://' + name; 
	let url = new URL(name); 
	let hostname = url.hostname; 
	if (!sites.includes(hostname)){
	sites.push(hostname); 
	chrome.storage.local.set({whitelistSites : sites}, function() {
		loadPage(); 
	}); 
	}
}

function addWhitelistPages(name) {
	name = 'https://' + name;  
	let url = new URL(name); 
	let pathname = url.href; 
	if (!pages.includes(pathname)){
	pages.push(pathname);  
	chrome.storage.local.set({whitelistPages : pages}, function() {
		loadPage(); 
	}); 
}
}