/* values (id, category, description, datetime, image, location, name, price) */
insert into event values ( 1, 'Music', 'test description', '2020-12-20T20:30:54', file_read('image_event_1.jpg'), 'genk', 'concert', 20.00 );
insert into event values ( 2, 'Music', 'test description2', '2020-11-21T21:31:55', file_read('image_event_1.jpg'), 'hasselt', 'name2', 21.00 );
insert into event values ( 3, 'Music', 'kerstmis', '2020-12-25T20:00:00', file_read('image_event_1.jpg'), 'thuis', 'kerstmis', 20.00 );
insert into event values ( 4, 'Music', 'nieuwjaar', '2021-01-01T00:00:00', file_read('image_event_1.jpg'), 'niet thuis', 'nieuwjaar', 30.00 );
insert into event values ( 5, 'Music', '2de kerstdag met familie', '2020-12-26T00:00:00', file_read('image_event_1.jpg'), 'zaal', '2de kerstdag', 15.00 );
insert into event values ( 6, 'Music', 'allerheiligen', '2020-11-01T00:00:00', file_read('image_event_1.jpg'), 'kerk', 'allerheiligen', 0.00 );
insert into event values ( 7, 'Music', 'allerzielen', '2020-11-02T00:00:00', file_read('image_event_1.jpg'), 'kerk', 'allerzielen', 0.00 );
insert into event values ( 8, 'Music', 'halloween', '2020-10-31T20:00:00', file_read('image_event_1.jpg'), 'zaal', 'halloween', 15.00 );
insert into event values ( 9, 'Music', 'nieuwe schooljaar', '2020-09-01T08:20:00', file_read('image_event_1.jpg'), 'school', 'nieuwe schooljaar', 765.00 );
insert into event values ( 10, 'Music', 'olympische spelen openingsceremonie', '2020-07-24T20:00:00', file_read('image_event_1.jpg'), 'Tokyo', 'olympische spelen', 80.00 );
insert into event values ( 11, 'Music', 'olympische spelen slotceremonie', '2020-07-24T20:00:00', file_read('image_event_1.jpg'), 'Tokyo', 'olympische spelen', 80.00 );
insert into event values ( 12, 'Music', 'de presentatie van deze website', '2020-06-15T00:00:00', file_read('image_event_1.jpg'), 'Tokyo', 'eindproef', 0.00 );
insert into event values ( 13, 'Music', 'amerikaanse thanksgiving', '2020-11-26T14:00:00', file_read('image_event_1.jpg'), 'bij mama', 'thanksgiving', 15.00 );
insert into event values ( 14, 'Theatre', 'black friday', '2020-11-27T00:00:00', file_read('image_event_1.jpg'), 'bij mama', 'black friday', 0.00 );
insert into event values ( 15, 'Theatre', 'wapensstilstand', '2020-11-11T11:11:11', file_read('image_event_1.jpg'), 'kerkhof', 'wapensstilstand', 0.00 );
insert into event values ( 16, 'Theatre', 'star ways day', '2020-05-04T05:04:00', file_read('image_event_1.jpg'), 'kelder', 'star wars day', 0.00 );
insert into event values ( 17, 'Theatre', 'presidentiele verkiezingen vs', '2020-11-03T00:00:00', file_read('image_event_1.jpg'), 'kieshokjes', 'presidentiele verkiezingen vs', 30.00 );
insert into event values ( 18, 'Theatre', 'Oracle''s annual convention brings together IT management, business decision-makers and line-of-business end users. Typically, the conference includes keynotes from leadership at Oracle and from other partner organizations. More 2,500 additional sessions and workshops focus on other IT and business-related topics.', '2020-09-21T10:00:00', file_read('image_event_1.jpg'), 'San Francisco', 'Oracle OpenWorld', 300.00 );
insert into event values ( 19, 'Theatre', 'AWS Re:invent is Amazon''s opportunity to update IT and business leaders on the latest features of its cloud service.The event features keynote announcements, training and certification opportunities, access to more than 2,000 technical sessions, a partner expo, and more.', '2020-11-30T11:00:00', file_read('image_event_1.jpg'), 'Las Vegas', 'AWS re:Invent', 30.00 );
insert into event values ( 20, 'Theatre', 'not the music one', '2020-08-06T00:00:00', file_read('image_event_1.jpg'), 'Las Vegas', 'Def Con 28', 320.00 );
insert into event values ( 21, 'Theatre', 'Fueling innovative software
Software development is changing fast. See what''s shaping software development today—from AI and cloud to data-driven apps and distributed computing—and learn how to put it to work for you. Best Price ends April 17.', '2020-07-13T00:00:00', file_read('image_event_1.jpg'), 'Portland, OR', 'O''Reilly Open Source Software Conference', 180.00 );
insert into event values ( 22, 'Theatre', 'VMWorld', '2020-08-30T00:00:00', file_read('image_event_1.jpg'), 'San Francisco', 'VMWorld', 250.00 );
insert into event values ( 23, 'Theatre', 'SpiceWorld', '2020-09-15T00:00:00', file_read('image_event_1.jpg'), 'Austin', 'SpiceWorld', 120.00 );
insert into event values ( 24, 'Theatre', 'GlueX', '2020-09-27T00:00:00', file_read('image_event_1.jpg'), 'Phoenix', 'GlueX', 54.00 );
insert into event values ( 25, 'Theatre', 'CodeCrafts', '2020-10-01T00:00:00', file_read('image_event_1.jpg'), 'Vienna', 'CodeCrafts', 79.00 );

insert into user values (1,'adminFirst','adminLast','adminPass','adminUser');
insert into user values (2,'Ruben','Neven','rubenpw','ruben');
insert into user values (3,'Tom','Haeldermans','tompw','tom');
insert into user values (4,'Michiel','Thomassen','michielpw','michiel');
insert into user values (5,'test','test','$2y$10$mUDfNQFM4HXQzgW98NXD6eS.CxHjVkRq2E20ZT.0YM2zJu1AsJblm','test');

insert into event_attendees values (2,2);
insert into event_attendees values (4,2);
insert into event_attendees values (8,2);
insert into event_attendees values (16,2);
insert into event_attendees values (6,3);
insert into event_attendees values (10,3);
insert into event_attendees values (12,3);
insert into event_attendees values (20,4);
insert into event_attendees values (22,4);
insert into event_attendees values (24,4);
