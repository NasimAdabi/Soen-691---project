
var fs = require('fs');
const fastcsv = require('fast-csv');


const opn = require('opn');

require.extensions['.txt'] = function (module, filename) {
    module.exports = fs.readFileSync(filename, 'utf8');
};

//  convert to csv file

var defectFile = require("./filesWithPostReleaseDefects.txt");
var defectArray =  defectFile.split(',');
var  count = {};
var result = '';
defectArray.forEach(function(i) { count[i] = (count[i]||0) + 1;});
Object.keys(count).forEach(function(fileName) {

    result = result + fileName + ',' + count[fileName] + '\n';

});

fs.writeFile("postRelease.csv", result, function(err) {
    if(err) {
        return console.log(err);
    }

    console.log("The file was saved!");
});

