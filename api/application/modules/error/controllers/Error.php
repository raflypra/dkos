<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Error extends MX_Controller {

    private $prefix         = 'error';

	function __construct() {
        parent::__construct();
    }

	public function error_404()
	{
        $data['pagetitle']  = 'Error';
        $data['instance']	= $this->prefix;
        $data['breadcrumb'] = ['Error' => $this->prefix.'/error_404'];
		
        $data['url']        = $this->input->post('url');
        $data['url1']        = $this->input->post('url1');

        $this->load->view('welcome_message', $data);
	}
}

