<?php
define ('SITE_ROOT', realpath(dirname(__FILE__)));

if (isset($_POST['upload_svg'])) {
		$i = 1;
        $t=time();
	$name = "Text-{$t}.svg";
	while(file_exists(SITE_ROOT."/images/pictures/{$name}")){
	++$i;
	$name = "Text-{$t}.svg";
	}
    if(file_put_contents(SITE_ROOT."/images/pictures/{$name}",$_POST['upload_svg'])){
    	echo $_POST['upload_svg']. " OK";
    } else {
        echo $_POST['upload_svg']. " KO";
    }
    exit;
} else {
    echo "No files uploaded ...";
}
?>