var table_issues = [];
var groups = [
    {"priority" : 0, "label":"bug", "title" : "Bugs Fixes (priority 0)", "numberOldTickets" : 0, "total" : 0},
    {"priority" : 1, "label":"missing blocking feature" , "title" :"Missing blocking feature (priority 1)", "numberOldTickets" : 0, "total" : 0},
    {"priority" : 2, "label":"missing feature", "title" :"Missing feature (priority 2)", "numberOldTickets" : 0, "total" : 0},
    {"priority" : 3, "label":"enhancement" , "title" :"Enhancements (priority 3)", "numberOldTickets" : 0, "total" : 0},
    {"priority" : 4, "label":"optional enhancement", "title" : "Optional Enhancements (priority 4)", "numberOldTickets" : 0, "total" : 0}
]

var filename = "ReleaseNotes";


module.exports = {
    "dataSource": "issues",
    "groupBy": {
        "Bugs Fixes (priority 0)" : ["bug"],
        "Missing blocking feature (priority 1)" : ["missing blocking feature"],
        "Missing feature (priority 2)" : ["missing feature"],
        "Enhancements (priority 3)" : ["enhancement"],
        "Optional Enhancements (priority 4)" : ["optional enhancement"]
    },
    "changelogFilename": filename.trim() + ".md",
    "onlyMilestone" : false,

    "template" : {

        "changelogTitle" : "# Release Notes \n\n",
        "release" : "## {{release}} \n\n This file contents the resume of all the issues closed in this release. \n If you need more informations contact the CJB. \n\n {{body}}\n\n",
        "releaseSeparator" : "\n***\n\n",

        issue : function (issue){

            // get the label index to be able to fill the table of issues
            var index = null;
            var labels = issue.labels.split(";");
            for (var i=0; i< labels.length; i++){
                index = getIndexFromLabel(labels[i]);
                if (index != null)
                    break;
            }

            if (issue.name.indexOf("Error on ") >= 0){
                // count the number of old ticket per label
                groups[index].numberOldTickets = groups[index].numberOldTickets + 1;
            } else {

                // construct a table a issue per label
                if (table_issues[index] == undefined)
                    table_issues[index] = [];

                table_issues[index].push(issue);
            }

            groups[index].total = groups[index].total + 1;

        },

        label : function(label){
            return label.label + ';';
        },

        group : function(group){
            var v = displayIssues(table_issues);
            return displayIssues(table_issues);
        }
    }
}

// extract the priority/index from the label
function getIndexFromLabel(label){
    for (var group=0; group <groups.length; group++){
        if (label === groups[group].label)
            return groups[group].priority;
    }

    return null;

}


// display the issues per label
function displayIssues(issues){
    var display = "";
    for (var group=0; group <groups.length; group++){

        var index = getIndexFromLabel(groups[group].label);

        var groupStr = '\n---\n ### **' + groups[group].title + '**';
        groupStr = groupStr + '&nbsp;<span style="background-color:darkgrey; color:white; border-radius:50%; width:36px; height:36px; text-align:center; margin-left:20px; padding:8px; border: 2px solid darkgrey;font: 18px Arial, sans-serif;">'+  groups[group].total +'</span> \n---\n' ;

        var table = '| Issue Number | Title | Labels | Open by | \r|-----|----------------------------------------------|-------------------------------|-----------|\r';

        if (issues[index] != undefined)
            for (var i=0; i< issues[index].length;i++){
                table = table + '| ' + issues[index][i].text
                    + ' |' + issues[index][i].name + '<br><br>' + extractDescription(issues[index][i].body).replace(/[\n\r]/g, '<br>')
                    + ' | ' + displayLabels(issues[index][i].labels)
                    + ' | ' + extractUser(issues[index][i].body)
                    + '|\r';
                // TODO reactivate link
                //table = table + '| [' + issues[index][i].text + ']('+ issues[index][i].url + ') |' + issues[index][i].name  + ' | ' + displayLabels(issues[index][i].labels) + '|\r';
            }

        // TODO reactivate link
        //groupStr = groupStr + table +  '\n\r And ' + groups[index].numberOldTickets + ' old issue, to see them follow this [link](https://github.com/CJB-Geneve/com-botalista-backend/issues?utf8=✓&q=is%3Aissue+is%3Aclosed+in%3Atitle+%25Error+on+label%3A'+ replaceAll(groups[group].label, " " , "+") +') \n\n ';
        groupStr = groupStr + table +  '\n\r And ' + groups[index].numberOldTickets + ' old issue, to see them follow this link. \n\n ';
        display = display + groupStr;
    }

    return display;
}

function replaceAll(str, find, replace) {
    while (str.includes(find))
        str = str.replace(find, replace);

    return str;
}

function extractUser(description) {
    var matchString1 = description.match(/Username : ([A-Za-zÀ-ÖØ-öø-ÿ]+[-_.]?[A-Za-zÀ-ÖØ-öø-ÿ]+)\n\n/g);
    var matchString2 = description.match(/<br\/>Username : ([A-Za-zÀ-ÖØ-öø-ÿ]+[-_.]?[A-Za-zÀ-ÖØ-öø-ÿ]+)<br\/>/g);

    if (matchString1 != null) {
        return matchString1.toString().replace(/\n\n/g, '').replace('Username : ', '')
    }else if (matchString2 != null)
        return matchString2.toString().replace(/<br\/>/g, '').replace('Username : ', '')
    else return '';
}

function extractDescription(description) {
    var index = description.indexOf('Description : ');
    if(index>0)
        return description.substring(index, description.length);
    return '';
}

// format the label to match a badge
function displayLabels(origLabels){
    var display = "";
    var labels = origLabels.split(";");

    for (var i=0; i< labels.length; i++){
        var color, background;
        var cssName = "default"
        switch(labels[i]){
            case "bug":
                background = "#F12D28";
                color = "#FFFFFF";
                cssName = "bug"
                break;
            case "missing blocking feature":
                background = "#FF8427";
                color = "#111111";
                break;
            case "missing feature":
                background = "#FFEC27";
                color = "#111111";
                break;
            case "enhancement":
                background = "#97C1EE" ;
                color = "#111111";
                break;
            case "optional enhancement":
                background = "#8CDEFF";
                color = "#111111";
                break;
            case "duplicate":
                background = "#D4D4D4";
                color = "#111111";
                break;
            case "invalid":
                background = "#FFFFFF";
                color = "#111111";
                break;
            case "paris":
                background = "#FF9BF8";
                color = "#111111";
                break;
            case "help wanted":
                background = "#128A0C";
                color = "#FFFFFF";
                break;
            case "question":
                background = "#D45090";
                color = "#FFFFFF";
                break;
            case "view":
                background = "#729E4C";
                color = "#111111";
                break;
            case "filter":
                background = "#884CB1";
                color = "#FFFFFF";
                break;
            default:
                background = "";
                color = "#111111";
                break;
        }

        // TODO reactivate label paris
        if (labels[i] != "paris")
            display = display + labels[i] + ' ' //'<span id="'+ cssName + '">' + labels[i] + '</span>' //'<span style="background-color:'+ background +'; color:'+ color +'; width:50px; text-align:center; margin-left:10px; margin-right:10px; padding:1px">' + labels[i] + '</span>'
    }
    return display;
}


