var fs = require('fs');
const request = require('request');
const cheerio = require('cheerio');
const readline = require('readline');
const opn = require('opn');

var filenames = [];
var churnesOutCome = [];

require.extensions['.txt'] = function (module, filename) {
    module.exports = fs.readFileSync(filename, 'utf8');
};

var churns = require("./churn.txt").toString();
churns = churns.trim();
churns = churns.split(/\n/);

for (var i = 0; i < churns.length; i++) {
  churns[i] = churns[i].replace(/^ +/gm,"");
  churns[i] = churns[i].trim();
}

for (var i = 0; i < churns.length; i++) {
  if (/\.java/.test(churns[i])){
    var churnFileName = /([^\/]+)(\.java)/.exec(churns[i]);
    var churnValue = /^[0-9]/.exec(churns[i]);

      if(churnFileName !=null ){
      var filename = churnFileName[0];
      filenames.push(filename);
      }

      if(churnValue !=null ){
            var value = churnValue[0];
            filenames.push(value);
      }

      churnesOutCome.push(filenames);
      filenames = [];

  }
}

console.log(churnesOutCome);
var result = '';
for (var i = 0; i < churnesOutCome.length; i++) {
  result = result + churnesOutCome[i][0] + ',' + churnesOutCome[i][1] + '\n';

}

fs.writeFile("churnMetrics.csv", result, function(err) {
    if(err) {
        return console.log(err);
    }

    console.log("The file was saved!");
});

