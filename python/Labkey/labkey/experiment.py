import ssl
import requests
import json

from requests.adapters import HTTPAdapter
from requests.packages.urllib3.poolmanager import PoolManager
from requests.exceptions import SSLError

## EXAMPLE
## -------

# from experiment import create_server_context, load_batch

# server_context = create_server_context('localhost:8080', 'Home', 'labkey', use_ssl=False)
# assay_id = 84
# batch_id = 1
# load_batch(assay_id, batch_id, server_context)

## --------
## /EXAMPLE

# _ssl.c:504: error:14077410:SSL routines:SSL23_GET_SERVER_HELLO:sslv3 alert handshake failure
# http://lukasa.co.uk/2013/01/Choosing_SSL_Version_In_Requests/
class SafeTLSAdapter(HTTPAdapter):
	def init_poolmanager(self, connections, maxsize, block=False):
		self.poolmanager = PoolManager(num_pools=connections,
									   maxsize=maxsize,
									   block=block,
									   ssl_version=ssl.PROTOCOL_TLSv1)

# TODO: consider moving this to a util, along with the SafeTLSAdapter
def create_server_context(domain, container_path, context_path=None, use_ssl=True):
	server_context = {
		'domain': domain,
		'container_path': container_path,
		'context_path': context_path
	}

	if use_ssl:
		scheme = 'https'
	else:
		scheme = 'http'
	scheme += '://'

	if use_ssl:
		session = requests.Session()
		session.mount(scheme, SafeTLSAdapter)
	else:
		# TODO: Is there a better way? Can we have session.mount('http')?
		session = requests

	server_context['scheme'] = scheme
	server_context['session'] = session

	return server_context

# TODO: Consider moving this to a util? action_url.py?
def build_url(controller, action, server_context):
	"""
	Builds a URL from a controller and an action. Uses the server_context to determine
	domain, context path, container, etc
	:param controller: The controller to use in building the URL
	:param action: The action to use in building the URL
	:param server_context:
	:return:
	"""
	sep = '/'

	url = server_context['scheme']
	url += server_context['domain']

	if server_context['context_path'] is not None:
		url += sep + server_context['context_path']

	url += sep + controller
	url += sep + server_context['container_path']
	url += sep + action

	return url

def load_batch(assay_id, batch_id, server_context):
	"""
	Loads a batch from the server.
	:param assay_id:
	:param batch_id:
	:return:
	"""
	load_batch_url = build_url('assay', 'getAssayBatch.api', server_context)
	session = server_context['session']

	payload = {
		'assayId': assay_id,
		'batchId': batch_id
	}

	headers = {
		'Content-type': 'application/json',
		'Accept': 'text/plain'
	}

	try:
		print load_batch_url
		print payload
		response = session.post(load_batch_url, data=json.dumps(payload), headers=headers)
		status = response.status_code

		if status == 200:
			decoded = json.JSONDecoder().decode(response.text)
			print(decoded)
		else:
			print(response.text)
			print(status)
	except SSLError as e:
		raise Exception("Failed to match server SSL configuration. Failed to load batch.")

def save_batch(run_group, container_path):

	if not isinstance(run_group, RunGroup):
		raise Exception('Expected to be a RunGroup')

	pass

# TODO: Move this to it's own file, under experiment
class RunGroup():

	def __init__(self, batch_protocol_id=0, runs=None, hidden=False):
		self.batch_protocol_id = batch_protocol_id
		self.hidden = hidden

		if runs is not None:
			self.runs = runs
		else:
			self.runs = []

	def to_json(self):

		json_runs = []
		for run in self.runs:
			json_runs.append(run.to_json())

		batch = {
			'batchProtocolId': self.batch_protocol_id,
			'runs': json_runs
		}
		data = {
			# TODO: Make sure it is always correct assay_id <- batch_protocol_id
			'assayId': self.batch_protocol_id,
			'batch': batch
		}

		return json.dumps(data)

# TODO: Move this to it's own file, under experiment
class Run():

	def __init__(self):
		self.experiments = []
		self._protocol = None
		self._file_path_root = None
		self.data_inputs = []
		self.data_outputs = []
		self.data_rows = []
		self.material_inputs = []
		self.material_outputs = []
		self.object_properties = []

	def to_json(self):
		data = {}
		return json.dumps(data)

	@staticmethod
	def from_data(self, data):
		return Run()

	@property
	def protocol(self):
		return self._protocol

	@protocol.setter
	def protocol(self, value):
		self._protocol = value

	@property
	def file_path_root(self):
		return self._file_path_root

	@file_path_root.setter
	def file_path_root(self, value):
		self._file_path_root = value