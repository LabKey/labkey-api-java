#
# Copyright (c) 2015 LabKey Corporation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
from __future__ import unicode_literals
import json

from requests.exceptions import SSLError
from utils import build_url

## EXAMPLE
## -------

# from utils import create_server_context
# from experiment import load_batch
#
# server_context = create_server_context('localhost:8080', 'CDSTest Project', 'labkey', use_ssl=False)
# assay_id = # provide one from your server
# batch_id = # provide one from your server
# run_group = load_batch(assay_id, batch_id, server_context)
#
# print(run_group.lsid)
# print(run_group.created_by)

## --------
## /EXAMPLE

# TODO Incorporate logging

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
		response = session.post(load_batch_url, data=json.dumps(payload), headers=headers)
		status = response.status_code

		if status == 200:
			decoded = json.JSONDecoder().decode(response.text)
			return RunGroup.from_data(decoded)
		else:
			print(response.text)
			print(status)
	except SSLError as e:
		raise Exception("Failed to match server SSL configuration. Failed to load batch.")

def save_batch(run_group, server_context):

	if not isinstance(run_group, RunGroup):
		raise Exception('save_batch() "run_group" expected to be instance of RunGroup')

	save_batch_url = build_url('assay', 'saveAssayBatch.api', server_context)

	# payload = {
	# 	'assayId': assay_id,
	# 	'batches': batches
	# }

	headers = {
		'Content-type': 'application/json',
		'Accept': 'text/plain'
	}

	raise NotImplementedError('save_batch is not ready yet')

class ExpObject(object):

	def __init__(self, **kwargs):
		self.lsid = kwargs.pop('lsid', None)
		self.name = kwargs.pop('name', None)
		self.id = kwargs.pop('id', None)
		self.row_id = self.id
		self.comment = kwargs.pop('comment', None)
		self.created = kwargs.pop('created', None)
		self.modified = kwargs.pop('modified', None)
		self.created_by = kwargs.pop('created_by', kwargs.pop('createdBy', None))
		self.modified_by = kwargs.pop('modified_by', kwargs.pop('modifiedBy', None))
		self.properties = kwargs.pop('properties', {})

# TODO: Move these classes into their own file(s)
class RunGroup(ExpObject):

	def __init__(self, **kwargs):
		super(RunGroup, self).__init__(**kwargs)

		self.batch_protocol_id = kwargs.pop('batch_protocol_id', kwargs.pop('batchProtocolId', 0))
		self.hidden = kwargs.pop('hidden', False)

		runs = kwargs.pop('runs', [])
		run_instances = []

		for run in runs:
			run_instances.append(Run.from_data(run))

		self.runs = run_instances

	@staticmethod
	def from_data(data):
		return RunGroup(**data['batch'])

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

class Run(ExpObject):

	def __init__(self, **kwargs):
		super(Run, self).__init__(**kwargs)

		self.experiments = kwargs.pop('experiments', [])
		self.file_path_root = kwargs.pop('file_path_root', kwargs.pop('filePathRoot', None))
		self.protocol = kwargs.pop('protocol', None)
		self.data_outputs = kwargs.pop('data_outputs', kwargs.pop('dataOutputs', []))
		self.data_rows = kwargs.pop('data_rows', kwargs.pop('dataRows', []))
		self.material_inputs = kwargs.pop('material_inputs', kwargs.pop('materialInputs', []))
		self.material_outputs = kwargs.pop('material_outputs', kwargs.pop('materialOutputs', []))
		self.object_properties = kwargs.pop('object_properties', kwargs.pop('objectProperties', []))

		# TODO: initialize protocol
		# self._protocol = None

		# initialize data_inputs
		data_inputs = kwargs.pop('data_inputs', kwargs.pop('dataInputs', []))
		data_inputs_instances = []

		for input in data_inputs:
			data_inputs_instances.append(Data.from_data(input))

		self.data_inputs = data_inputs_instances

	@staticmethod
	def from_data(data):
		return Run(**data)

	def to_json(self):
		data = {}
		return json.dumps(data)

class ProtocolOutput(ExpObject):

	def __init__(self, **kwargs):
		super(ProtocolOutput, self).__init__(**kwargs)

		self.source_protocol = kwargs.pop('source_protocol', kwargs.pop('sourceProtocol', None))
		self.run = kwargs.pop('run', None) # TODO Check if this should be a Run instance
		self.target_applications = kwargs.pop('target_applications', kwargs.pop('targetApplications'), None)
		self.successor_runs = kwargs.pop('successor_runs', kwargs.pop('sucessorRuns', None)) # sic
		self.cpas_type = kwargs.pop('cpas_type', kwargs.pop('cpasType', None))

	@staticmethod
	def from_data(data):
		return ProtocolOutput(**data)

class Data(ProtocolOutput):

	def __init__(self, **kwargs):
		super(Data, self).__init__(**kwargs)

		self.data_type = kwargs.pop('data_type', kwargs.pop('dataType', None))
		self.data_file_url = kwargs.pop('data_file_url', kwargs.pop('dataFileURL', None))
		self.pipeline_path = kwargs.pop('pipeline_path', kwargs.pop('pipeline_path', None))
		self.role = kwargs.pop('role', None)

	@staticmethod
	def from_data(data):
		return Data(**data)