#!/usr/bin/env python

from distutils.core import setup
setup(name='LabKey',
      version='0.19',
      description='Python client API for LabKey Server',
      long_description = open('README.txt').read() + open('NEWS.txt').read(),
      license="Apache License 2.0",
      author='Elizabeth Nelson',
      author_email='eknelson@labkey.com',
      maintainer='Brian Connolly',
      maintainer_email='brian@labkey.com',
      url='https://www.labkey.org/wiki/home/Documentation/page.view?name=python',
      download_url='http://www.labkey.com/download-labkey-server',
      packages=['labkey'],
      classifiers=[
        'Development Status :: 4 - Beta',
        'Environment :: Console',
        'Intended Audience :: Science/Research',
        'Intended Audience :: System Administrators',
        'License :: OSI Approved :: Apache Software License',
        'Operating System :: MacOS',
        'Operating System :: Microsoft',
        'Operating System :: POSIX',
        'Programming Language :: Python :: 2',
        'Topic :: Scientific/Engineering'
        ]
     )
