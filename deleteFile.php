<?php
define ('SITE_ROOT', realpath(dirname(__FILE__)));
        echo SITE_ROOT."/images/pictures/{$_POST['file']}";
        unlink(SITE_ROOT."/images/pictures/{$_POST['file']}");
?>