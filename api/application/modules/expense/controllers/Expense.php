<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Expense extends CI_Controller {

	public function index()
	{
		$user_id = $this->input->post('member_id');

		$data = $this->m_global->get_data_all('expense', null, ['expense_user_id' => $user_id,'expense_status' => '1']);

		echo json_encode($data);

	}

}
