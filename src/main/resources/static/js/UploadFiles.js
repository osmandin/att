// $Id: UploadFiles.js,v 1.16 2016-11-15 11:39:45-04 ericholp Exp $

var numfiles = 0;
var filedatastring = "";
var totalfilesize = 0;
var filedatadisplaytable = "";
var avail = 0;
var totalmax=150E9;    // 150GB
var peruploadmax=50E9; // 50GB

function loadfunc() {
    gatherdisplayfiledata(0);
}

function clearfiles() {
    try {
        var i = 0;
        $("div").each(
            function () {
                if ($(this).attr("id") == "fileobj") {
                    if (i != 0) {
                        $(this).remove();
                    }
                    i++;
                }
            }
        );

        // overkill, but what the heck?
        $("input[type=file]").each(
            function () {
                $(this).replaceWith($(this).val('').clone(true));
            }
        );
        numselectedfile = 0;
        totalfilesize = 0;
        avail = 0;
        $('#displayfiledata').html("");
    } catch (ex) {
        alert("clearfiles: " + ex);
    }
};


function displayBytes(bytes) {
    try {
        if (bytes <= 1000) {
            return bytes + "B";
        } else if (bytes < 1E6) {
            return (bytes / 1000).toFixed(2) + "kB";
        } else if (bytes < 1E9) {
            return (bytes / 1E6).toFixed(2) + "MB";
        } else if (bytes < 1E12) {
            return (bytes / 1E9).toFixed(2) + "GB";
        } else if (bytes < 1E15) {
            return (bytes / 1E12).toFixed(2) + "TB";
        } else {
            return bytes + "B";
        }
    } catch (ex) {
        alert("displayBytes: " + ex);
        return "unknown";
    }
}


function gatherfiledata() {
    try {
        numfiles = 0;
        filedatastring = "";
        filedatadisplaytable = "<table border=\"0\">";
        totalfilesize = 0;

        var sep = "";
        $("input[type=file]").each(
            function () {
                var name = this.name;
                var files = this.files;
                for (var i = 0; i < files.length; i++) {
                    var file = files[i];

                    var filename = file.name;
                    var filesize = file.size;
                    var moddate = file.lastModifiedDate;
                    totalfilesize += file.size;

                    filedatadisplaytable += "<tr><td width=\"20px\"><td>" + filename + "</td><td width=\"5px\"><td>" + displayBytes(filesize) + "</td></tr>";
                    filedatastring += sep + filename + "|" + filesize + "|" + moddate;

                    sep = ";";
                    numfiles++;
                }
            }
        );

        filedatadisplaytable += "</table>";
    } catch (ex) {
        alert("gatherfiledata: " + ex);
    }
}

function gatherfiledata_validate_and_submit() {
    try {

        //alert("checking");
        //alert("Context:" + context);

        gatherfiledata();
        //alert("checked");
        if (numfiles == 0) {
            alert("Please select one or more files");
            return;
        }
        $.ajax({
            method: "POST",
            url: context + "CheckSpace",
        }).done(function (availfilesystembytes) {
            var availfilesystembytes = parseInt(availfilesystembytes);
            var availbytes = peruploadmax;
            var curtotalmax = totalmax;
            if (availfilesystembytes < totalmax) {
                curtotalmax = availfilesystembytes;
            }
            if (curtotalmax < peruploadmax) {
                availbytes = curtotalmax;
            }
            if (availbytes < 0) {
                availbytes = 0;
            }
            if (availbytes < totalfilesize) {
                alert("The selected files are too large.  The total size must be less than " + displayBytes(availbytes));
            } else {
                $("#filedata").val(filedatastring); // to pass file data to servlet (did not see a way, on the server side to determine modification dates)
                //                                       uses a hidden input variable
                $("#form").submit();
            }
        });
    } catch (ex) {
        alert("gatherfiledata_validate_and_submit: " + ex);

        if (numfiles == 0) {
            alert("Numfiles undefined?");
        }
    }
}


function gatherdisplayfiledata(promptuser) {
    try {
        gatherfiledata();
        if (promptuser && numfiles == 0) {
            alert("Please select one or more files");
            $("#displayfiledata").html("");
            return;
        }
        $.ajax({
            method: "POST",
            url: context + "CheckSpace",
        }).done(function (availfilesystembytes) {
            var availfilesystembytes = parseInt(availfilesystembytes);
            var availbytes = peruploadmax;
            var curtotalmax = totalmax;
            if (availfilesystembytes < totalmax) {
                curtotalmax = availfilesystembytes;
            }
            if (curtotalmax < peruploadmax) {
                availbytes = curtotalmax;
            }
            if (availbytes < 0) {
                availbytes = 0;
            }
            if (availbytes < totalfilesize) {
                alert("The selected files are too large.  The total size must be less than " + displayBytes(availbytes));
            } else {


                var infostr =
                    "You have selected the following file(s):" +
                    "<br/>" +
                    filedatadisplaytable +
                    "<br/>" +
                    "Total filesize=" + displayBytes(totalfilesize) +
                    "<br/>" +
                    "<br/>";

                if (promptuser || totalfilesize > 0) {
                    $("#displayfiledata").html(infostr);
                }

            }

        });
    } catch (ex) {
        alert("gatherdisplayfiledata: " + ex);
    }
}

function addit(el) {
    try {
        var copy = $("#fileobj").clone(true);
        copy.find("#file").val('');
        copy.find("#minus").show();
        $(el).prev().prev().after(copy);
    } catch (ex) {
        alert("addit: " + ex);
    }
}

function removeit(el) { // don't use "remove" since it will simply run builtin js remove
    try {
        $(el).prev().prev().prev().prev().prev().remove();
        $(el).prev().prev().prev().prev().remove();
        $(el).prev().prev().prev().remove();
        $(el).prev().prev().remove();
        $(el).prev().remove();
        $(el).remove();
    } catch (ex) {
        alert("removeit: " + ex);
    }
}
