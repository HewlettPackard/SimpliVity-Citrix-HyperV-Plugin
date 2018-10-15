# Automation for Citrix Cloud on HPE SimpliVity (Hyper-V)

HPE SimpliVity plugin for Citrix Cloud is an automated tool to install or uninstall citrix cloud connector of a resource location on SimpliVity Hyper-V server.

## Release Notes

This is the first version of HPE SimpliVity plugin for Citrix Cloud which installs and configures Citrix Cloud connector. Major aspects of this plugin that are covered in this release are:

- Proxy implementation is a preview feature (beta) in version 1.0
- Secure SSL/HTTPS enabled on SimpliVity OmniCube controller (OVC) IP address to connect using REST APIs
- DHCP Server is required for assignment of IP’s to Cloud Connector VMs
- VM template is tested on Windows 2016 Operating System

Few caveats of this Hyper-V plugin:
- SCVMM templates are not stored in SimpliVity and base VM satisfying the template requirements are used to perform SimpliVity clone
- Hosts in SimpliVity clone are automatically selected during deployment. Live migration should be working in the SCVMM setup to migrate VM to the user specified host. If live migration is not supported, VM placement is not guaranteed to reside on the specified host
- SCVMM should run on default port 8100

## Citrix Cloud Connector

Citrix Cloud Connector acts as an interface between the Citrix Cloud and on-premise resource location. This helps in managing the hosts and virtual desktops from the cloud by removing the need for complex solutions and networking. Each resource location is recommended to have more than one cloud connector to provide high availability (HA). However, it is laborious to configure or de-configure Citrix Cloud Connector whenever a resource location is created or deleted.

This plugin automates the installation of Citrix Cloud connectors by deploying virtual machines (VMs), adding them to Active Directory (AD), downloading Citrix Cloud Connector and installing it.

## Version

Citrix Cloud Connector installation on HPE SimpliVity storage using Microsoft SCVMM plugin is tested on the following versions:

#### HPE SimpliVity
- OmniCube Controller: **3.7**
- API Version:	**1.8**

#### Microsoft SCVMM 
- Client:  **Windows 2016**
- Build: **4.0.2314.0**

## Usage
To use this plugin, download the plugin from `package/HpeCitrixPlugin.zip` and follow the steps in the user guide, located in `docs` directory, to install and configure SimpliVity plugin for SCVMM

At a high level, download the HPE SimpliVity Citrix Cloud Connector plugin (connector plugin)
1. Download the plugin `HpeCitrixPlugin.zip`
2. Extract the plugin to `C:\Users\Public`
3. Login to SCVMM Manager and upload the `CitrixSimplivity.zip` Add-in from the path `C:\Users\Public\HpeCitrixPlugin`
4. Close and reopen the SCVMM Management console to reflect to access the new plugin
5. Accessing the plug-in by click on `Simplivity-Citrix Connector` in `VMs and Services`

## Plugin Development
To develop new features or fix bugs in the plugins, refer the code in the `dev` directory

- Code for the JAVA application is available in `dev/GUI`
- Powershell scripts to configure and deconfigure SCVMM are available in `dev/HpeCitrixPlugin/Configuration`
- Manifest files related to the new SCVMM Add-in are available in `dev/HpeCitrixPlugin`
