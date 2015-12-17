insert into newsy(tytul, tresc, data) values ('artykul1', 'wazna tresc', sysdate);
insert into newsy(tytul, tresc, data) values ('artykul2', 'wazna tresc2', sysdate);
insert into newsy(tytul, tresc, data) values ('artykul3', 'artykul bez tagow', sysdate);
insert into tagi (newsy_id, tag) values (1, 'sport');
insert into tagi (newsy_id, tag) values (1, 'hehe');
insert into tagi (newsy_id, tag) values (2, 'sport');
insert into tagi (newsy_id, tag) values (2, 'seba');