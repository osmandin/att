package edu.mit.controllers;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// $Id: Format.java,v 1.12 2016-11-15 11:47:41-04 ericholp Exp $

public class Format {
    @SuppressWarnings("unused")
    private static final String rcsinfo = "$Id: Format.java,v 1.12 2016-11-15 11:47:41-04 ericholp Exp $";

    private final static Logger LOGGER = Logger.getLogger(Format.class.getCanonicalName());

    private long totalfilesize;
    private String totalfilesizestr;

    // ------------------------------------------------------------------------
    public String showavailbytes(String path) {
        File f = new File(path);
        long ab = f.getUsableSpace();
        return displayBytes_bi(ab);
    }

    // ------------------------------------------------------------------------
    public String displayBytes_bi(long bytes) {
        DecimalFormat df = new DecimalFormat("0.##");

        if (bytes <= 1024) {
            return String.format("%dB", bytes);
        } else if (bytes < 1048576) {
            return df.format((float) bytes / 1024) + "kB";
        } else if (bytes < 1073741824L) {
            return df.format((float) bytes / 1048576L) + "MB";
        } else if (bytes < 1099511627776L) {
            return df.format((float) bytes / 1073741824L) + "GB";
        } else if (bytes < 1125899906842624L) {
            return df.format((float) bytes / 1099511627776L) + "TB";
        } else {
            return String.format("%dB", bytes);
        }
    }

    // ------------------------------------------------------------------------
    public String displayBytes(long bytes) {
        DecimalFormat df = new DecimalFormat("0.##");

        if (bytes <= 1000) {
            return String.format("%dB", bytes);
        } else if (bytes < 1000000L) {
            return df.format((float) bytes / 1000) + "kB";
        } else if (bytes < 1000000000L) {
            return df.format((float) bytes / 1000000L) + "MB";
        } else if (bytes < 1000000000000L) {
            return df.format((float) bytes / 1000000000L) + "GB";
        } else if (bytes < 1000000000000000L) {
            return df.format((float) bytes / 1000000000000L) + "TB";
        } else {
            return String.format("%dB", bytes);
        }
    }

    // ------------------------------------------------------------------------
    public String displayInGB_bi(long bytes) {
        DecimalFormat df = new DecimalFormat("0.##");
        return df.format((float) bytes / 1073741824L);
    }

    // ------------------------------------------------------------------------
    public String displayInGB(long bytes) {
        //DecimalFormat df = new DecimalFormat("0.##");
        // return df.format((float) bytes / 1000000000L);

        return FileUtils.byteCountToDisplaySize(bytes);
    }

    // ------------------------------------------------------------------------
    public String returnLastModifiedDateTime(String filename) {
        File file = new File(filename);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(file.lastModified());
    }

    // ------------------------------------------------------------------------
    public boolean changeFileLastModDateTime(String filename, String newLastModDateTime) {
        try {
            File file = new File(filename);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = sdf.parse(newLastModDateTime);
            file.setLastModified(newDate.getTime());
            return true;
        } catch (ParseException ex) {
            //ex.printStackTrace();
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    // ------------------------------------------------------------------------
    public String convertFileInfoMonthDateToMonthIntString(String fileInfoMonthDate) {
        if (fileInfoMonthDate.equals("Jan")) {
            return "01";
        }
        if (fileInfoMonthDate.equals("Feb")) {
            return "02";
        }
        if (fileInfoMonthDate.equals("Mar")) {
            return "03";
        }
        if (fileInfoMonthDate.equals("Apr")) {
            return "04";
        }
        if (fileInfoMonthDate.equals("May")) {
            return "05";
        }
        if (fileInfoMonthDate.equals("Jun")) {
            return "06";
        }
        if (fileInfoMonthDate.equals("Jul")) {
            return "07";
        }
        if (fileInfoMonthDate.equals("Aug")) {
            return "08";
        }
        if (fileInfoMonthDate.equals("Sep")) {
            return "09";
        }
        if (fileInfoMonthDate.equals("Oct")) {
            return "10";
        }
        if (fileInfoMonthDate.equals("Nov")) {
            return "11";
        }
        if (fileInfoMonthDate.equals("Dec")) {
            return "12";
        }
        return "unknown: " + fileInfoMonthDate;
    }


    // ------------------------------------------------------------------------
    public long getTotalfilesize() {
        return totalfilesize;
    }

    // ------------------------------------------------------------------------
    public String getTotalfilesizestr() {
        return totalfilesizestr;
    }

    // ------------------------------------------------------------------------
    public List<FileData> parseFileInfo(String fileinfo) {

        List<FileData> fileinfodata = new ArrayList<FileData>();

        totalfilesize = 0;

        if (fileinfo != null) {
            String[] fileinfoarray = fileinfo.split("\\;");
            if (fileinfoarray.length > 0) {
                int filenum = 0;
                for (String thisfiledetails : fileinfoarray) {
                    FileData info = new FileData();
                    String[] fileparts = thisfiledetails.split("\\|");
                    int i = 0;
                    for (String thisfilepart : fileparts) {
                        if (i == 0) {
                            info.setName(thisfilepart);
                        } else if (i == 1) {
                            totalfilesize += Long.parseLong(thisfilepart);
                            info.setSize(Long.parseLong(thisfilepart));
                            info.setNicesize(displayBytes(Long.parseLong(thisfilepart)));
                        } else if (i == 2) {
                            String[] dateparts = thisfilepart.split("\\s+");
                            if (dateparts.length >= 5) {
                                String month = dateparts[1];
                                String monthint = convertFileInfoMonthDateToMonthIntString(month);
                                String date = dateparts[3] + "-" + monthint + "-" + dateparts[2] + " " + dateparts[4];
                                info.setLastmoddatetime(date);
                            } else {
                                info.setLastmoddatetime(thisfilepart);
                            }
                        } else {
                            LOGGER.log(Level.SEVERE, "filenum={0}: thisfilepart={1}", new Object[]{filenum, thisfilepart});
                        }
                        i++;
                    }
                    fileinfodata.add(info);
                }
            }
            totalfilesizestr = displayInGB(totalfilesize);
        }
        return fileinfodata;
    }

}
