<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Assignment extends CI_Controller {

	public function index()
	{
		$group_id = $this->input->post('group_id');

		$join = [
					['table' => 'form', 'on' => 'form_id=assignment_form_id'],
					['table' => 'group', 'on' => 'group_id=assignment_group_id'],
				];

		$data = $this->m_global->get_data_all('assignment', $join, ['group_id' => $group_id,'assignment_status' => '1']);

		for ($i=0; $i < count($data); $i++) { 
			$data[$i]->form_json = json_encode( generateJSONForm(strEncrypt($data[$i]->form_id) ), JSON_PRETTY_PRINT);
		}
		// echo "<pre>";
		// print_r($data); exit;
		echo json_encode($data);

	}

	public function add_assignment(){
		// $assignment_id = '2';
		// $user_id = '4';
		// $json = '{"count":1,"step1":{"title":"Rumah","desc":"Rumah","fields":[{"id":"35","key":"kepalakeluarga","type":"edit_text","value":"wwwwwwwwwww","hint":"Kepala Keluarga","label":"Kepala Keluarga","order":"1"},{"id":"36","key":"nomor","type":"edit_text","value":"333333333333333","hint":"Nomor Rumah","label":"Nomor Rumah","order":"2","v_numeric":{"value":"true","err":"Only numeric"}},{"id":"37","key":"hakmilik","type":"spinner","value":"Sewa","hint":"Hak Milik","label":"Hak Milik","order":"3","values":["Milik Sendiri","Sewa"]},{"id":"38","key":null,"type":"check_box","value":"paket 2","hint":null,"label":"Add On","order":"4","options":[{"id":"38","key":"service perminggu","text":"Service Perminggu","value":"true"},{"id":"38","key":"pengecekan perhari","text":"Pengecekan Perhari","value":"true"}],"values":["Service Perminggu","Pengecekan Perhari"]},{"id":"39","key":null,"type":"radio","value":"paket 1","hint":null,"label":"Paket","order":"5","options":[{"id":"39","key":"paket 1","text":"Paket 1"},{"id":"39","key":"paket 2","text":"Paket 2"}],"values":["Paket 1","Paket 2"]},{"id":"40","key":"ktp","type":"choose_image","uploadButtonText":"KTP","value":"\/storage\/sdcard\/KTP-SD-2017.jpg"}]}}';
		$assignment_id  = $this->input->post('assignment_id');
		$user_id 		= $this->input->post('user_id');
		$json 			= $this->input->post('json');
		$imgname 	    = $this->input->post('imgname');

		$data['data_assignment_id'] = $assignment_id;
		$data['data_user_id'] 		= $user_id;
		$data['data_create_date']   = date('Y-m-d H:i:s');

		$result = $this->m_global->insert('data', $data);
		$data_json = json_decode($json, true);
		$counter1 = 0;
		$counter2 = 0;
		$imgcounter = 0;

		if($result['status']){
			for ($i=0; $i < $data_json['count']; $i++) {
				$no = $i + 1;
				$fields = $data_json['step'.$no]['fields'];
				$counter2 = $counter2 + count($fields);
				for ($a=0; $a < count($fields); $a++) { 
					$data_detail['detail_data_id'] 		= $result['id'];
					$data_detail['detail_entry_id'] 	= $fields[$a]['id'];
					if($fields[$a]['type'] == 'check_box'){
						$check_box = $fields[$a]['options'];
						$check_box_val = [];
						for ($c=0; $c < count($check_box); $c++) {
							if ($check_box[$c]['value'] == 'true') {
							 	$check_box_val[$c] = $check_box[$c]['text'];
							}
						}
						$data_detail['detail_value'] 		= json_encode($check_box_val);
					}elseif($fields[$a]['type'] == 'choose_image'){
						if($fields[$a]['value'] != ''){
							$data_detail['detail_value'] 		= $imgname.$imgcounter.'.jpg';
							$imgcounter++;
						}else{
							$data_detail['detail_value'] 		= null;
						}
					}else{
						$data_detail['detail_value'] 		= $fields[$a]['value'];
					}
					$add_ass = $this->m_global->insert('data_detail', $data_detail);
					if($add_ass['status']){
						$counter1++;
					}
				}
			}

			if($counter1 == $counter2){
				$response["error"] 		= FALSE;
				echo json_encode($response);
			}else{
				$response["error"] 		= TRUE;
				echo json_encode($response);
			}
		}else{
			$response["error"] 		= TRUE;
			echo json_encode($response);
		}

		// echo "<pre>";
		// print_r($fields); exit;
	}

	public function upload_image(){
		$image = $this->input->post('image');
    	$name  = $this->input->post('name');
    	$decoded=base64_decode($image);
		
		if(file_put_contents('assets/form_pict/'.$name,$decoded)){
			if($result){
				echo "1";
			}else{
				echo "0";
			}
		}else{
			echo "0";
		}
	}

}
