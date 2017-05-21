# cs0320 Term Project

**Team Members:** Abhishek Sharma, Paul Touma, Dalton Thuku, Ji Ryoo, Simon Belete

**Project Idea:** A fake news detector browser extension

**Mentor TA:** _Put your mentor TA's name and email here once you're assigned one!_

## Project Requirements
Project Idea: 
There is a big problem of sensationalized and sometimes completely false news stories on the internet. We decided to address these issues by creating a browser extension that would warn people when they were looking at content on a page that could be unverified or false information. 

Research: 
We created a short survey and asked people, the majority of whom used the chrome web browser, about their experiences with "fake-news." Every person we asked had had an experience where they read something on the internet that was valse or unverified. Most of the people discovered what they were reading was fake by looking the unverified claims up on google and looking for other articles about what they read. However, some people went on believing that what they had read was true for some time before they realized it wasn't, usually by being told by someone else that what they had read was false. 

Requirements: 
Our project will be a chrome browser extension that users can install on their browsers. The extension will monitor all texts on the pages they visit, and will alert them when they are reading text that is highly likely to be false or unverified. The algorithm will compare the text on the pages with a database of compiled fake news stories from trusted claim-monitoring websites like snopes.com. We will use this database because most users said that they would only use the service if they knew how the claims were being verified. When there is a high likelihood that certain text came from a falsified claim, the text on the page would be highlighted for the user to see. This was the way most people said they would want to be alerted of fake claims in their browser. Then, the user would be able to hover over the highlighted text and a dialog-box would show up over the text, telling the user what the likelihood of the claim being fake is, along with links to articles from trusted sources about the subject. 
The extension would have an icon in the browser tray, which the user could click to turn the extension on/off. The user could also use this icon to add sites to a list of 'trusted' websites on which the extension would automatically be turned off.  

## Project Specs and Mockup
Mockup: https://app.moqups.com/as300/fmExOEpkV9/view
Program Specifications:
Front End Specifications
Article Page
Highlighting titles on pages that give a ‘high risk’ of fake news (what that means exactly will be defined after some experimentation)
Users can hover/click on the titles for more information about the article, which will tell users: 
The overall emotion score of the article
How the score differs from other articles in its category/categories
(If applicable) Other similar stories from better sources based on the keywords present in the article
Highlighting paragraphs that are especially high in emotion scores
On hover/click: 
The specific emotion scores of that article
Extension Menu
Temporarily ‘Pause’ the extension
Ability to ‘Whitelist’ certain pages
Show results to good pages, checkmark, or validation
Our own separate page
Using the article’s keywords, show links to similar real articles
Display data mining results, showing relevant keywords, percentages and scores explaining how those stack up against other real articles

Minimum Processing Specifications
Categorizing Articles
Create emotion score ‘benchmarks’ for each article category based on the good and bad articles we mine in those categories, and develop a sensible cutoff measure to determine whether a given article is ‘overly emotional’ 
Matching Articles by Keyword
For different articles we find, store them based on their keywords, and be able to give examples of ‘good’ articles with strong matches on certain keywords

Minimum Data-Mining Specifications
Use IBM’s Watson to process the language in the articles and build a database
The articles would be an even number of “valid” and “fake” news in a variety of different subjects


## Project Design Presentation
_A link to your design presentation/document will go here!_

## How to Build and Run
_A necessary part of any README!_
