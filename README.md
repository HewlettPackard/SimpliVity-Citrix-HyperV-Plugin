# SCVMM SimpliVity plugin for Citrix Cloud

HPE SimpliVity plugin for citrix cloud is an automated tool to install or uninstall citrix cloud connector of a resource location on SimpliVity Hyper-V server.

## Citrix Cloud Connector

Citrix cloud connector acts as an interface between the Citrix cloud and resource location. This helps in managing the hosts and virtual desktops from the cloud by removing the need for complex solutions and networking. Each resource location is recommended to have more than one cloud connector to provide high availability (HA). However, it is laborious to configure or de-configure cloud connector whenever a resource location is created or deleted. 

This plugin automates the installation of citrix cloud connectors by deploying virtual machines (VMs), adding them to Active Directory (AD), downloading Citrix cloud connector and installing it.

## Version

Citrix cloud connector installation on HPE SimpliVity storage using Microsoft SCVMM plugin is tested on the following versions:
##### HPE SimpliVity
+ OmniCube Controller: **3.7.0.46**
+ API Version:	**1.8**
##### Microsoft SCVMM 
+ Cient:  **Windows 2016**
+ Build: **4.0.2314.0**

## Usage
To use this plugin, download the plugin from `package/HpeCitrixPlugin.zip` and follow the steps in the user guide, located in `docs` directory, to install and configure SimpliVity plugin for SCVMM

At a high level, download the HPE SimpliVity Citrix Cloud Connector plugin (connector plugin)
1. Downlaod the plugin `HpeCitrixPlugin.zip`
2. Extract the plugin to `C:\Users\Public`
3. Login to SCVMM Manager and upload the `CitrixSimplivity.zip` Add-in from the path `C:\Users\Public\HpeCitrixPlugin`
4. Close and reopen the SCVMM Management console to reflect to access the new plugin
5. Accessing the plug-in by click on `Simplivity-Citrix Connector` in `VMs and Services`

## Plugin Development
To develop new features or fix bugs in the plugins, refer the code in the `dev` directory

- Code for the JAVA application is available in `dev/GUI`
- Powershell scripts to configure and deconfigure SCVMM are available in `dev/HpeCitrixPlugin/Configuration`
- Manifest files related to the new SCVMM Add-in are available in `dev/HpeCitrixPlugin`
