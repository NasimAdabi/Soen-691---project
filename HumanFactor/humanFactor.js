var fs = require('fs');
const request = require('request');
const cheerio = require('cheerio');
const readline = require('readline');
const opn = require('opn');

require.extensions['.txt'] = function (module, filename) {
    module.exports = fs.readFileSync(filename, 'utf8');
};

 

var defects = [];

// reading file and making an array out of each line

var Commits = require("./log1.txt").toString();
var output = Commits.split(/commit([\s\S]+?)commit/)
output.splice(0, 1);

for (var i = 0; i < output.length; i++) {

  var lines;
  var filenames = [];
  var defect = [];
  lines = output[i].split('\n');
  lines.shift();
  for(var j = 0;j < lines.length;j++){


    // removes the empty lines
    if (lines[j] == '') {
      lines.splice(i, 1);
    }

    // removes the space before defect id
    if (/^\s|\s$/.test(lines[j])) {
    lines[j]= lines[j].replace(/^\s+|\s+$/g,'')
    
  }

    // gets author's email address
    if (/Author/.test(lines[j])) {

        var obj= /<(.*)>/.exec(lines[j]);
          if(obj != null ){
        var author = obj[1].toString();
        defect.push(author);
      }
    }

    // gives the date of committing (defect)
    if (/date/gi.test(lines[j])) {
      obj = /(?<=date:   ).*$/gi.exec(lines[j]);
      if(obj !=null ){
        var date = obj[0];
        defect.push(date);
      }

     
    }

    //  defect id,which we use for screen scraping
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


var allFiles = [];
for (var i = 0; i < defects.length; i++) {
  
  for (var j = 0; j < defects[i][defects[i].length-1].length; j++) {
    allFiles.push(defects[i][defects[i].length-1][j]);
    allFiles.push(defects[i][0]);

  }
}


var result = [];
var count = 1;
for (var i = 0; i < allFiles.length; i=i+2) {
  for (var j = i+2; j < allFiles.length; j+=2) {
    if (allFiles[j] == allFiles[i]) {
      // cheaking the emails
      if (allFiles[j+1] != allFiles[i+1]) {

        count++;


      }
      allFiles.splice(j,2);
    }
  }
  result.push(allFiles[i])
  result.push(count);
  count = 1;
}


var output = '';
for (var i = 0; i < result.length; i+=2) {
  output = output + result[i] + ',' + result[i+1] + '\n';

}


fs.writeFile("Authors.csv", output, function(err) {
    if(err) {
        return console.log(err);
    }

    console.log("The file was saved!");
});



