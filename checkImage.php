<?php
$indir = array_filter(scandir('./images/pictures'), function($item) {
    return !is_dir('./images/pictures' . $item);
});
$indir = array_diff($indir,Array(".",".."));
$filesString = implode(';', $indir);
        echo $filesString;
?>