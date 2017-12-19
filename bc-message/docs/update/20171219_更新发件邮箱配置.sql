update bc_option_item set value_ = '13318786102@139.com'
  where pid = (select id from bc_option_group where key_ = 'bc.mailSender') and key_ = 'username';
update bc_option_item set value_ = 'gf81800088'
  where pid = (select id from bc_option_group where key_ = 'bc.mailSender') and key_ = 'password';
update bc_option_item set value_ = 'smtp.139.com'
  where pid = (select id from bc_option_group where key_ = 'bc.mailSender') and key_ = 'host';