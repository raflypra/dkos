<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Login extends CI_Controller {

	public function index()
	{
		echo md5_mod('dkos');
	}

	public function login_api(){

        $response = array("error" => FALSE);
 
        if (isset($_POST['email']) && isset($_POST['password'])) {
         
            // receiving the post params
            $email = $_POST['email'];
            $password = $_POST['password'];
         
            // get the user by email and password
            $user = $this->cek_login_api($email, $password);
         
            if ($user != false) {
                // user is found
                $response["error"] = FALSE;
                $response["uid"] = $user->user_id;
                $response["user"]["name"] = $user->user_firstname.' '.$user->user_lastname;
                $response["user"]["email"] = $user->user_email;
                $response["user"]["created_at"] = $user->user_createddate;
                $response["user"]["picture"] = $user->user_picture;
                $response["user"]["username"] = $user->user_username;
                $response["user"]["group_id"] = "";
                $response["user"]["group_name"] = "";
                echo json_encode($response);
            } else {
                // user is not found with the credentials
                $response["error"] = TRUE;
                $response["error_msg"] = "Email atau password salah";
                echo json_encode($response);
            }
        } else {
            // required post params is missing
            $response["error"] = TRUE;
            $response["error_msg"] = "Mohon isi email dan password";
            echo json_encode($response);
        }
    }

    public function member_login(){

		$member_id = $this->input->post('member_id');

		$data = $this->m_global->get_data_all('users',null,['user_id' => $member_id]);

		$data = ['data' => $data];
		echo json_encode($data);
	}

	public function member_edit()
    {
        $data['user_name']        = $this->input->post('name');
        $data['user_username']    = $this->input->post('username');
        $data['user_address']     = $this->input->post('address');
        $data['user_gender']      = $this->input->post('gender');

        $result = $this->m_global->update('users', $data, ['user_id' => $this->input->post('member_id')]);

        $response   = [];

        if($result){
            $response['error'] = FALSE;
            echo json_encode($response);
        }else{
            $response['error'] = TRUE;
            echo json_encode($response);
        }
    }

    public function member_token(){
        $data['user_device_id']     = $this->input->post('token');
        $this->m_global->update('users', $data, ['user_id' => $this->input->post('member_id')]);
    }

    public function member_token_check(){
        $data['member_id']     = $this->input->post('member_id');
        $token = $this->m_global->get_data_all('users', null, ['user_id' => $this->input->post('member_id')], 'user_device_id')[0]->user_device_id;
        if($token != ''){
            if($token != $this->input->post('token')){
                echo "1";
            }else{
                echo "0";
            }
        }else{
            echo "2";
        }
    }

	public function cek_login_api($user_data, $password){
        
        $password       = md5_mod( $password );

        $result = $this->m_global->get_data_all( 'users', null, ['user_username' => $user_data, 'user_password' => $password] )[0];

        if ( ! empty( $result ) )
        {
            return $result;

        } else {

            return false;
        }
    }

}

/* End of file login.php */
/* Location: ./application/modules/login/controllers/login.php */