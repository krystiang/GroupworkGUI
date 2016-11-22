<?php
$indir = array_filter(scandir('./images/backgrounds/'), function($item) {
    return !is_dir('./images/backgrounds/' . $item);
});
$indir = array_diff($indir,Array(".",".."));
$filesString = implode(';', $indir);
        echo $filesString;
?>