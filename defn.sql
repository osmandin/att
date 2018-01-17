CREATE DATABASE submit;

USE submit;

CREATE TABLE departments (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  name varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE users (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  username char(16) DEFAULT NULL,
  firstname char(64) DEFAULT NULL,
  lastname char(64) DEFAULT NULL,
  isadmin tinyint(4) DEFAULT '0',
  email varchar(64) DEFAULT NULL,
  enabled tinyint(4) DEFAULT '0',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE map (
  departmentid int(10) unsigned NOT NULL,
  userid int(10) unsigned NOT NULL,
  departmentactive tinyint(4) DEFAULT '0',
  PRIMARY KEY (departmentid,userid),
  KEY fk_departmentuser_user_idx (userid),
  CONSTRAINT fk_departmentuser_department FOREIGN KEY (departmentid) REFERENCES departments (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_departmentuser_user FOREIGN KEY (userid) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE approvedRsas (
  id int(11) NOT NULL AUTO_INCREMENT,
  datetime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  accession varchar(256) DEFAULT NULL,
  rsaid int(11) DEFAULT NULL,
  departmentname varchar(256) DEFAULT NULL,
  sendername varchar(256) DEFAULT NULL,
  senderemail varchar(256) DEFAULT NULL,
  approvername varchar(256) DEFAULT NULL,
  approveremail varchar(256) DEFAULT NULL,
  description varchar(1024) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE onlineSubmissionRequest (
  id int(11) NOT NULL AUTO_INCREMENT,
  ssaid int(11) DEFAULT NULL,
  department varchar(256) DEFAULT NULL,
  address varchar(1024) DEFAULT NULL,
  name varchar(1024) DEFAULT NULL,
  email varchar(1024) DEFAULT NULL,
  phone varchar(32) DEFAULT NULL,
  departmenthead varchar(1024) DEFAULT NULL,
  signature varchar(1024) DEFAULT NULL,
  date date DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
CREATE TABLE rsas (
  id int(11) NOT NULL AUTO_INCREMENT,
  startyear varchar(4) DEFAULT NULL,
  endyear varchar(4) DEFAULT NULL,
  description varchar(4096) DEFAULT NULL,
  extent bigint(20) DEFAULT NULL,
  extentstr varchar(32) DEFAULT NULL,
  ssaid int(11) NOT NULL DEFAULT '0',
  transferdate date DEFAULT NULL,
  accessionnumber varchar(64) DEFAULT NULL,
  createdby varchar(256) DEFAULT NULL,
  approved tinyint(4) DEFAULT NULL,
  deleted tinyint(4) DEFAULT 0,
  idx int(11) DEFAULT 0,
  PRIMARY KEY (id,ssaid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE rsaFileData (
  id int(11) NOT NULL AUTO_INCREMENT,
  rsaid int(11) DEFAULT NULL,
  name varchar(4096) DEFAULT NULL,
  size bigint DEFAULT NULL,
  nicesize varchar(256) DEFAULT NULL,
  lastmoddatetime datetime DEFAULT NULL,
  status varchar(256) DEFAULT 'failed: skipped',
  idx int(11) DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE ssas (
  id int(11) NOT NULL AUTO_INCREMENT,
  departmenthead varchar(1024) DEFAULT null,
  recordid varchar(1024) DEFAULT null,
  departmentid int(11) DEFAULT null,
  creationdate date DEFAULT null,
  recordstitle varchar(1024) DEFAULT null,
  effectivedate date DEFAULT null,
  expirationdate date DEFAULT null,
  retentionalertdate date DEFAULT null,
  retentionschedule varchar(1024) DEFAULT null,
  otherformattypes varchar(2028) DEFAULT null,
  recorddescription varchar(2048) DEFAULT null,
  retentionperiod varchar(1024) DEFAULT null,
  descriptionstandards varchar(2048) DEFAULT null,
  createdby varchar(256) DEFAULT null,
  editdate date  DEFAULT null,
  enabled tinyint(4)  DEFAULT 0,
  approved tinyint(4) DEFAULT 0,
  deleted tinyint(4) DEFAULT 0,
  IP varchar(64) DEFAULT null,
  onlinesubmission tinyint(1) DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE ssaAccessRestrictions (
  id int(11) NOT NULL AUTO_INCREMENT,
  ssaid int(11) DEFAULT NULL,
  restriction varchar(1024) DEFAULT NULL,
  idx int(11) DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE ssaContacts (
  id int(11) NOT NULL AUTO_INCREMENT,
  ssaid int(11) DEFAULT NULL,
  name varchar(1024) DEFAULT NULL,
  phone varchar(32) DEFAULT NULL,
  address varchar(1024) DEFAULT NULL,
  email varchar(1024) DEFAULT NULL,
  idx int(11) DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE ssaCopyrights (
  id int(11) NOT NULL AUTO_INCREMENT,
  ssaid int(11) DEFAULT NULL,
  copyright varchar(1024) DEFAULT NULL,
  idx int(11) DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE ssaFormatTypes (
  id int(11) NOT NULL AUTO_INCREMENT,
  ssaid int(11) DEFAULT NULL,
  formattype varchar(1024) DEFAULT NULL,
  idx int(11) DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
