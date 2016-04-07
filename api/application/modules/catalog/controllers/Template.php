<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Catalog extends CI_Controller {

	public function index()
	{
		$data = $this->m_global->get_data_all('catalog', null, ['catalog_status' => '1']);
		for ($i=0; $i < count($data); $i++) { 
			$cover = $this->m_global->get_data_all('catalog_detail',null,['catdetail_catalog_id' => $data[$i]->catalog_id],'catdetail_picture')[0]->catdetail_picture;
			$data[$i]->catalog_cover_picture = $cover;
			$data[$i]->catalog_lastupdate = tgl_format($data[$i]->catalog_lastupdate);
		}

		$data = ['data' => $data];
		echo json_encode($data);

	}

	public function catalog_detail(){
		$cat_id = $this->input->post('cat_id');

		$data = $this->m_global->get_data_all('catalog_detail', null, ['catdetail_catalog_id' => $cat_id, 'catdetail_status' => '1']);

		$data = ['data' => $data];
		echo json_encode($data);
	}

	public function test(){
		$data = [];
		for ($i=0; $i < 15; $i++) { 
			$data[$i] = '1_edisi2015_'.($i+1).'.jpg';
		}
		// echo "<pre>";
		// print_r($object); exit;

		echo json_encode($data);
	} 

}
