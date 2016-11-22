<?php
define ('SITE_ROOT', realpath(dirname(__FILE__)));

if (isset($_FILES['upload_file'])) {
	echo "Test";
	$i = 2;
	$name = substr($_FILES['upload_file']['name'], 0, -4);
	$typ = substr($_FILES['upload_file']['name'], -4, 4);
	while(file_exists(SITE_ROOT."/images/pictures/{$_FILES['upload_file']['name']}")){
		$_FILES['upload_file']['name'] = "{$name}-{$i}{$typ}";
		++$i;
	}
    if(move_uploaded_file($_FILES['upload_file']['tmp_name'], SITE_ROOT."/images/pictures/{$_FILES['upload_file']['name']}")){
        echo $_FILES['upload_file']['name']. " OK";
    } else {
        echo $_FILES['upload_file']['name']. " KO";
    }
    exit;
} else {
    echo "No files uploaded ...";
}
?>