<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/*
    START Core Helper        
*/

function csrf_init(){
    $CI =& get_instance();  

    $csrf   = strEncrypt('csrf');
    $value  = strEncrypt(date('YmdHis'));

    $CI->session->unset_userdata($csrf);    
    $CI->session->set_userdata([$csrf => $value]);
}

function csrf_get_token(){
    $CI =& get_instance();
    $csrf   = strEncrypt('csrf');
    $data   = @$CI->session->userdata($csrf);

    $data   = ($data != '') ? $data : '-';

    return $data;
}

function strEncrypt($str, $forDB = FALSE){
    $CI =& get_instance();  
    $key    = $CI->config->item('encryption_key');

    $str    = ($forDB) ? 'md5(concat(\'' . $key . '\',' . $str . '))' : md5($key . $str);   
    return $str;
}

function generate_salt(){
    $CI =& get_instance();  
    $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    $charactersLength = strlen($characters);
    $randomString = '';
    for ($i = 0; $i < 16; $i++) {
    $randomString .= $characters[rand(0, $charactersLength - 1)];
    }
    return $randomString;
}

function md5_mod($str, $salt = ""){

    // $str = md5(md5($str).$salt);
    $str = strEncrypt($str);
    return $str;
}

function bulan($bulan)
{
    $aBulan = ['Januari','Februari','Maret','April','Mei','Juni','Juli','Agustus','September','Oktober','November','Desember'];
    
    return $aBulan[$bulan];
}

function bulan_min($bulan)
{
    $aBulan = ['Jan','Feb','Mar','Apr','Mei','Jun','Jul','Ags','Sep','Okt','Nov','Des'];
    
    return $aBulan[$bulan];
}

function tgl_format( $tgl, $showtime = 0, $nomin = null )
{
    if( !is_null($tgl) and $tgl != '' and $tgl != '0000-00-00' ){

    $tanggal    = date('d', strtotime($tgl));
    $bulan      = ( $nomin == null ? bulan_min( date('n', strtotime($tgl))-1 ) : bulan( date('n', strtotime($tgl))-1 ) );
    $tahun      = ( $nomin == null ? date('y', strtotime($tgl)) : date('Y', strtotime($tgl)) );

    $hasil      = $tanggal.' '.$bulan.' '.$tahun;

    if ( $showtime == 1 ) $jam = ' ' . substr($tgl, -8) . '';
    $hasil = ( $showtime == 1 ) ? $hasil . @$jam : $hasil;

    } else {
        $hasil = '';
    }

    return $hasil;
}

function religion_to_index($index)
{
    $religion = [
    'Islam'     => '1',
    'Protestan' => '2',
    'Katolik'   => '3',
    'Hindu'     => '4',
    'Budha'     => '5',
    'Konghucu'  => '6'
    ];

    return $religion[$index];
}

function multi_encript($id)
{
    $data = [];
    foreach ($id as $key => $value) {
        $data[] = strEncrypt($value);
    }

    return $data;
}

function display($var, $exit = null)
{
    echo '<pre>';print_r($var);echo '</pre>';
    if ( $exit )
    {
        exit();
    }
}

/*
    END Core Helper        
*/

function in_array_r($needle, $haystack, $strict = false) {
    foreach ($haystack as $item) {
        if (($strict ? $item === $needle : $item == $needle) || (is_array($item) && in_array_r($needle, $item, $strict))) {
            return true;
        }
    }

    return false;
}

function Terbilang($x)
{
    $abil = array("", "satu", "dua", "tiga", "empat", "lima", "enam", "tujuh", "delapan", "sembilan", "sepuluh", "sebelas");
    if ($x < 12)
        return " " . $abil[$x];
    elseif ($x < 20)
        return Terbilang($x - 10) . "belas";
    elseif ($x < 100)
        return Terbilang($x / 10) . " puluh" . Terbilang($x % 10);
    elseif ($x < 200)
        return " seratus" . Terbilang($x - 100);
    elseif ($x < 1000)
        return Terbilang($x / 100) . " ratus" . Terbilang($x % 100);
    elseif ($x < 2000)
        return " seribu" . Terbilang($x - 1000);
    elseif ($x < 1000000)
        return Terbilang($x / 1000) . " ribu" . Terbilang($x % 1000);
    elseif ($x < 1000000000)
        return Terbilang($x / 1000000) . " juta" . Terbilang($x % 1000000);
}

function rgb2hex($rgb) {
   $hex = '';
   $hex .= str_pad(dechex($rgb[0]), 2, "0", STR_PAD_LEFT);
   $hex .= str_pad(dechex($rgb[1]), 2, "0", STR_PAD_LEFT);
   $hex .= str_pad(dechex($rgb[2]), 2, "0", STR_PAD_LEFT);

   return $hex;
}

function tree_view($table, $where) {
    $CI = &get_instance();
    
    // get parent               
        $CI->db->select('*')->from($table);
        $CI->db->where($where);

        $query  = $CI->db->get();
        $parent     = $query->result();
        return $parent;
}

function tree_child($table, $where, $prefix) {
    $CI = &get_instance();
    
    // get parent               
    $CI->db->select('*')->from($table);
    $CI->db->where($where);

    $query  = $CI->db->get();
    $data   = $query->result();

    if (count($data) > 0) {
        $str    = "<ul>";

        foreach ($data as $rows) {  
            $name   = $prefix . "_long";
            $id     = $prefix . "_id";
            @$str .= '<li data-jstree=\'{ "opened" : true }\'><span onClick="f_edit(\''.$rows->$id.'\')">'. @$rows->$name.'</span>';

            // check lagi dong ah :D
            $str .= tree_child($table, [$prefix . "_parent" => $rows->$id], $prefix);

            $str .= '</li>';

        }

        $str    .= "</ul>";
    }       

    return @$str;
}

function sidebar_menu( $menu, $url )
{
    foreach ( $menu as $key => $value ) 
    {
        echo 
            '<li ' . 
            /*
                Jika nama controller dari menu helper sama dengan controller
            */
            ( $value['controller'] == $url 
                ? 'class="start active open"' 
                : ''
            ).'>

            <a ' .
            /*
                Mempunyai sub menu atau tidak
                untuk link href
            */
            (is_array($value['link']) 
                ? 'href="javascript:;"' 
                : 'class="ajaxify" href="'.base_url($value['link']).'"') . 
            '>

            <i class="icon-'.$value['icon'].'"></i>

            <span class="title">'.$value['name'].'</span>' .

            /*
                Mempunyai sedang aktif
            */
            ($key == 0 
                ? '<span class="selected"></span>' 
                : ''
            ) .

            /*
                Mempunyai sub menu atau tidak
                untuk menampilkan arrow
            */
            (is_array($value['link']) 
                ? '<span class="arrow ' .
                    ( $value['controller'] == $url 
                    ? 'open'
                    : '')
                . '"></span>'
                : ''
            ) . '</a>';
            
            sub_menu( $value, $url, '2' );

        echo '</li>';
    }
}

function sub_menu( $value, $url, $segment ){

    /*
        Mempunyai sub menu atau tidak
        untuk menampilkan sub link
    */

    if ( is_array($value['link']) )
    {
        echo '<ul class="sub-menu">';

        $CI =& get_instance();

        /*
            Menampilkan sub menu
        */

        foreach ( $value['link'] as $kSub => $kValue ) 
        {
            $sub_url = $CI->uri->segment($segment);

            /*
                Jika controller parent sama dengan uri sebelumnya
                dan controller sekarang sama dengan uri sekarang
            */

            echo '<li ' .
                ($kValue['controller'] == $sub_url && $value['controller'] == $url 
                    ? 'class="active"' 
                    : ''
                ) . '>

                <a ' .

                /*
                    Jika mempunyai sub, maka href=javascript (tidak ada link)
                    jika tidak, maka href berisi link
                */

                (is_array($kValue['link']) 
                    ? 'href="javascript:;"' 
                    : 'class="ajaxify" href="'.base_url($kValue['link']).'"'
                ) . 

                '>
                    <i class="icon-'.$kValue['icon'].'"></i>
                    ' . $kValue['name'] .

                /*
                    Jika mempunyai sub dan controller parent sama dengan uri sekarang
                    maka arrow open (sub menu sedang aktif)
                    selain itu, hanya menampilkan arrow (mempunyai sub menu tapi tidak aktif)
                */

                (is_array($kValue['link']) && $kValue['controller'] == $sub_url  
                    ? '<span class="arrow open"></span>' 
                    : 
                        (is_array($kValue['link'] ) 
                            ? '<span class="arrow"></span>'
                            : ''
                        )
                ) . '
                    </a>';
                
                /*
                    cek lagi gan sub menu level selanjutnya
                */
                    
                sub_menu( $kValue, $sub_url, $segment+1 );

             echo '</li>';
        }
        echo '</ul>';
    }
}

function cek_interfal( $start, $end )
{
    $date1      = date_create( $start );
    $date2      = date_create( $end );
    $interfal   = date_diff( $date1, $date2 );

    $year       = ( $interfal->format('%y') != '0' ? $interfal->format('%y') .' Tahun ' : '');
    $mount      = ( $interfal->format('%m') != '0' ? $interfal->format('%m') .' Bulan ' : '');
    $day        = ( $interfal->format('%d') != '0' ? $interfal->format('%d') .' Hari ' : '');
    $hours      = ( $interfal->format('%h') != '0' ? $interfal->format('%h') .' Jam ' : '');
    $minute     = ( $interfal->format('%i') != '0' ? $interfal->format('%i') .' Menit ' : '');
    $second     = ( $interfal->format('%s') != '0' ? $interfal->format('%s') .' Detik ' : '');

    $hasil      = $year.$mount.$day.$hours.$minute.$second;

    return $hasil;
}

function cek_interfal_minim( $start, $end )
{
    $date1      = date_create( $start );
    $date2      = date_create( $end );
    $interfal   = date_diff( $date1, $date2 );

    $year       = ( $interfal->format('%y') != '0' ? $interfal->format('%y') .' thn ' : null);
    $mount      = ( $interfal->format('%m') != '0' ? $interfal->format('%m') .' bln ' : null);
    $day        = ( $interfal->format('%d') != '0' ? $interfal->format('%d') .' hr ' : null);
    $hours      = ( $interfal->format('%h') != '0' ? $interfal->format('%h') .' jam ' : null);
    $minute     = ( $interfal->format('%i') != '0' ? $interfal->format('%i') .' mnt ' : null);
    $second     = ( $interfal->format('%s') != '0' ? $interfal->format('%s') .' dtk' : null);

    $hasil      = ( $year ? $year : ( $mount ? $mount : ( $day ? $day : ( $hours ? $hours : ( $minute ? $minute : ( $second ? $second : '') ) ) ) ) );

    return $hasil;
}

function getAuth( $index = null )
{
    $CI =& get_instance();
    $session = $CI->session->all_userdata();
    if(is_null($index)){
        return $session;
    } else {
        return $session[$index];
    }
}

function getEmp( $index = null )
{
    $CI =& get_instance();
    $session = $CI->session->all_userdata();
    if(is_null($index)){
        return $session['user_data'];
    } else {
        $name = 'employee_'.$index;
        return $session['user_data']->$name;
    }
}

function checkAbsen()
{
    $CI         =& get_instance();
    $session    = $CI->session->all_userdata('user_data')['user_data'];
    $id         = $session->employee_id;
    $role       = $session->employee_role;
    $date       = date('Y-m-d');

    if ( $role != 2 )
    {
        $CI->db->select('absen_id')->from('absen');
        $CI->db->where(['absen_employee_id' => $id, 'DATE(absen_date)' => $date]);

        $query  = $CI->db->get();
        $absen = $query->result();
    }
    
    if ( @$absen[0]->absen_id or $role == 2 ) 
    {
        return TRUE;

    } else {

        return FALSE;

    }

}

function getProject( $idproject, $value = null )
{
    $CI =& get_instance();

    $CI->db->select('*')->from('project');
    $CI->db->where(['project_id' => $idproject]);

    $query      = $CI->db->get();
    $project    = $query->result();

    if( $project ){
        if( $value ){
            return $project[0]->$value;
        } else {
            return $project;
        }
    } else {
        return null;
    }
}

function getModul( $idmodul, $value = null )
{
    $CI =& get_instance();

    $CI->db->select('*')->from('modul');
    $CI->db->where(['modul_id' => $idmodul]);

    $query      = $CI->db->get();
    $modul      = $query->result();

    if( $modul ){
        if( $value ){
            return $modul[0]->$value;
        } else {
            return $modul;
        }
    } else {
        return null;
    }
}

function getSubsList( $idemp )
{
    $CI =& get_instance();

    $CI->db->select('*')->from('subscribe');
    $CI->db->where(['subs_employee_id' => $idemp]);

    $query      = $CI->db->get();
    $modul      = $query->result();

    $projid     = [];
    foreach ($modul as $value) {
        $projid[] = $value->subs_project_id;
    }
    $project_id = implode(',', $projid);
    return $project_id;

}

function subscribe( $idproject = null, $uns = null )
{
    if ( getEmp('role') == '1' )
    {
        $CI =& get_instance();
        $data =  ['subs_employee_id' => getEmp('id'), 'subs_project_id' => $idproject];
        
        if ( $uns ) {
            $CI->db->delete('subscribe', $data);
        } else {
            $CI->db->insert('subscribe', $data );
        }

    }
}

function treeRec( $arr )
{
    if( !is_null( $arr )){
        echo "<ul>";
        foreach ($arr as $key => $value) {
            echo "<li>";
            echo $value['name'];
            treeRec( $value['sub'] );
            echo "</li>";
        }
        echo "</ul>";
    }
}

function getQuote( $part = 'quote' )
{
    $CI =& get_instance();

    $CI->db->select('*')->from('employee');
    $CI->db->where(['employee_quote_day' => '1']);

    $query      = $CI->db->get();
    $quote      = $query->result();

    $name = 'employee_'.$part;
    return $quote[0]->$name;

}

function getQuoteList()
{
    $CI =& get_instance();

    $CI->db->select('*')->from('employee');
    $CI->db->where(['employee_id <>' => getEmp('id')]);
    $CI->db->order_by('employee_id','random');

    $query      = $CI->db->get();
    $quote      = $query->result();

    return $quote;

}

function updateSession()
{
    $CI =& get_instance();
    $empdata = $CI->m_global->get_data_all('employee', null, ['employee_id' => getEmp('id')]);
    $CI->session->set_userdata('user_data', $empdata[0]);
}

function uang($var,$dec="0"){
    if(empty($var)) return 0;
    return 'Rp. ' . number_format(str_replace(',','.',$var),$dec,',','.');
}

function dis_uang($var){
    $set1 = str_replace('Rp. ', '', $var);
    $set2 = str_replace('.', '', $set1);
    return $set2;
}

function gcm_sender($fields){
    $headers = ['Authorization: key=' . GCM_API,'Content-Type: application/json'];
    $ch = curl_init();
    curl_setopt( $ch, CURLOPT_URL, GCM_URL );
    curl_setopt( $ch, CURLOPT_POST, true );
    curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
    curl_setopt( $ch, CURLOPT_POSTFIELDS, json_encode( $fields ) );
    curl_setopt( $ch, CURLOPT_SSL_VERIFYHOST, false);
    curl_setopt( $ch, CURLOPT_SSL_VERIFYPEER, false);
    $result = curl_exec($ch);
    curl_close($ch);

    return $result;
}