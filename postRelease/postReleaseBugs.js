
var fs = require('fs');
const cheerio = require('cheerio');
const readline = require('readline');
const request = require('request');
const opn = require('opn');

require.extensions['.txt'] = function (module, filename) {
    module.exports = fs.readFileSync(filename, 'utf8');
};

 

var defects = [];

// read input file and make an array of each line

var logCommits = require("./log1.txt").toString();
var logExtract = logCommits.split(/commit([\s\S]+?)commit/)
logExtract.splice(0, 1);


for (var i = 0; i < logExtract.length; i++) {

  var lines;
  var filenames = [];
  var defect = [];

  // each commit line will be an element of the array
  lines = logExtract[i].split('\n');
  lines.shift();

  // a loop to cleanup each commit using regular expressions
  for(var j = 0;j < lines.length;j++){

    // thid removes the empty lines

    if (lines[j] == '') {
      lines.splice(i, 1);
    }

    // removes the space before defect id

    if (/^\s|\s$/.test(lines[j])) {
    lines[j]= lines[j].replace(/^\s+|\s+$/g,'')
    }

    // this gets author's email address

    if (/Author/.test(lines[j])) {
        var obj= /<(.*)>/.exec(lines[j]);
          if(obj != null ){
        var author = obj[1].toString();
        defect.push(author);
      }
    }

    // this gives us the date of commit(defect)

    if (/date/gi.test(lines[j])) {
      obj = /(?<=date:   ).*$/gi.exec(lines[j]);
      if(obj !=null ){
        var date = obj[0];
        defect.push(date);
      }
    }

    // the defect id, which we use for screen scraping

    if (/\w*-\w*\./.test(lines[j])) {
      var obj = /\w*-\w*/.exec(lines[j]);
      if(obj !=null ){
      var commitId = obj[0];
      defect.push(commitId);
      }
    }

    if (  /([^\/]+)\.java/.test(lines[j])) {
      var obj = /([^\/]+)\.java/.exec(lines[j]);
      if(obj !=null ){
      var filename = obj[0];
      filenames.push(filename);
      }
    }

  }
  defect.push(filenames);
  defects.push(defect);
}

var urlAndAssociatedFiles = [];


for (var i = 0; i < defects.length; i++) {

  var defectIdarry = defects[i];
  var defectId = defectIdarry[2];
  var associateFiles = defectIdarry[3];
  if (  typeof defectId === 'string' && associateFiles.length != 0) {
    var defectUrl = 'https://issues.apache.org/jira/si/jira.issueviews:issue-xml/' + defectId + '/' + defectId +'.xml';
    var temp = [];
    temp.push(defectUrl);
    temp.push(associateFiles);
    urlAndAssociatedFiles.push(temp);
  }
}
var filesWithBug =[];
processArray(urlAndAssociatedFiles);



function delay() {
  return new Promise(resolve => setTimeout(resolve, 100));
}

async function delayedLog(item,end) {

  // notice that we can await a function that returns a promise

  await delay();

  request(item[0], (error, response, html) => {
    if(!error && response.statusCode == 200) {
      const $ = cheerio.load(html);
      $tit = $('title').text();
      $title = $('type').text();
      if ($title == 'Bug' ) {
        $resolution = $('resolution').text();
        if ($resolution == 'Fixed') {
          $resolved = $('resolved').text();
          var mydate = new Date($resolved);
          var anotherDate = new Date('Tue Apr 21 2015');
          if (mydate > anotherDate) {
            //console.log(item[1]);
            filesWithBug.push(item[1]);
          }else {
        console.log(" has date error");
      }
        }else {
        console.log(" is not Fixed");
      }
      }else {
        console.log(" is not a Bug");
      }

  }});

  if (end === 0) {
    console.log("files: " + filesWithBug.length);
    fs.writeFile("filesWithPostReleaseDefects.txt", filesWithBug, function(err) {
    if(err) {
        return console.log(err);
    }

    console.log("The file was saved!");
});
  }


}
async function processArray(array) {

  for (var l = 0; l < array.length; l++) {

    if (l < array.length-1 ) {
      await delayedLog(array[l]);
    }else {
      await delayedLog(array[l],0);
    }

  }


}

