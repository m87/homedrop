SQLite format 3   @     s   	           	                                                 s -�         	��V ��                                                                 ��tablefileTagfileTagCREATE TABLE `fileTag` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `file_id` BIGINT NOT NULL , `tagEntity_id` BIGINT NOT NULL )l�;tabletagstagsCREATE TABLE `tags` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `name` VARCHAR NOT NULL )�|�WtablefilesfilesCREATE TABLE `files` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `name` VARCHAR NOT NULL , `path` VARCHAR NOT NULL , `checkSum` BIGINT NOT NULL , `version` BIGINT NOT NULL , `lastChange` BLOB NOT NULL , `owner_id` BIGINT NOT NULL )P++Ytablesqlite_sequencesqlite_sequenceCREATE TABLE sqlite_sequence(name,seq)�9�QtableusersusersCREATE TABLE `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `name` VARCHAR NOT NULL , `password` VARCHAR NOT NULL , `home` VARCHAR NOT NULL ,  UNIQUE (`name`)))= indexsqlite_autoindex_users_1users         ��                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 !
 )testuser2pass2home_testuser2	 'testuserpasstestuser_home
      ��                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         testuser2
testuser	   � �                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           	users
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        �   ��V � �      ��tablerulesrulesCREATE TABLE `rules` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `type` INTEGER NOT NULL , `file_id` BIGINT , `body` VARCHAR NOT NULL )��tablefileTagfileTagCREATE TABLE `fileTag` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `file_id` BIGINT NOT NULL , `tagEntity_id` BIGINT NOT NULL )l�;tabletagstagsCREATE TABLE `tags` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `name` VARCHAR NOT NULL )�|�WtablefilesfilesCREATE TABLE `files` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `name` VARCHAR NOT NULL , `path` VARCHAR NOT NULL , `checkSum` BIGINT NOT NULL , `version` BIGINT NOT NULL , `lastChange` BLOB NOT NULL , `owner_id` BIGINT NOT NULL )P++Ytablesqlite_sequencesqlite_sequenceCREATE TABLE sqlite_sequence(name,seq)�9�QtableusersusersCREATE TABLE `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `name` VARCHAR NOT NULL , `password` VARCHAR NOT NULL , `home` VARCHAR NOT NULL ,  UNIQUE (`name`)))	= indexsqlite_autoindex_users_1users   