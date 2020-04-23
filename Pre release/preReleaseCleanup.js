
var fs = require('fs');
const fastcsv = require('fast-csv');
const ws = fs.createWriteStream("out.csv");

const opn = require('opn');

require.extensions['.txt'] = function (module, filename) {
    module.exports = fs.readFileSync(filename, 'utf8');
};

// TODO: Dictionary to csv file
// R or Js for each row in csv file in row of metricsFile
var defectFile = require("./filesWithPreReleaseDefects.txt");
var defectArray =  defectFile.split(',');
// console.log(defectArray);


// uniqueCount = ["a","b","c","d","d","e","a","b","c","f","g","h","h","h","e","a"];

var  count = {};
// columnDelimiter = args.columnDelimiter || ',';
// lineDelimiter = args.lineDelimiter || '\n';
var result = '';
defectArray.forEach(function(i) { count[i] = (count[i]||0) + 1;});
Object.keys(count).forEach(function(fileName) {

    result = result + fileName + ',' + count[fileName] + '\n';

});
// console.log(result);
fs.writeFile("preRleaseDefects.csv", result, function(err) {
    if(err) {
        return console.log(err);
    }

    console.log("The file was saved!");
});

