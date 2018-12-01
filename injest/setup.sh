#!/bin/bash

# Configure git submodule goodies

echo Configuring git to automatically update submodules.
git config --local submodule.recurse true # fetch/pull also sync up subnodules 
git submodule init # Initialize the submodules in the local repo
git pull # Now we will pull, which will pull the submodules as well.

echo Checking for Python 3
# Ensure that Python 3 is available
if command -v python3 &>/dev/null; then
    echo Python 3 already installed.
else
    # Install Python 3
    echo Installing Python 3
    brew install python3
fi

echo Checking for virtualenv
if ! hash virtualenv 2>/dev/null; then
  echo Installing virtrualenv.
  pip3 install virtualenv
else
  echo Virtualenv already installed.
fi

#  Setup virtual env and satisfy equirement.txt
echo Setting up virtualenv for this repository..
virtualenv -p $(which python3) .venv
source .venv/bin/activate

echo Installing requirements.
pip3 install -r requirements.txt

# Tell the user about whats been done. There is a lot of noise so we will make this prominent:
echo "*******************************************************************************"
echo "*******************************************************************************"
echo 
echo                                Setup Complete! 
echo
echo Python3 based VirtualEnv created, To activate run \'source .venv/bin/activate\'
echo The git setting \'submodule.recurse\' has been enabled, which means that fetch 
echo and pull operations will automatically include the submodules.
echo 
echo
echo "*******************************************************************************"
echo "*******************************************************************************"

