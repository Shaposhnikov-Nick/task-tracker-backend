insert into task_tracker."Role" (id, name) values (1, 'ADMIN');

insert into task_tracker."Users" (id, login, password, email_confirmed, blocked, profile_id)
values (1,'test','test',false,false, 1);

insert into task_tracker."UserRole" (user_id, role_id) values (1, 1);
