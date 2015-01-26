require 'cloudshare-sdk/cs_low_api'

module CloudshareSDK
  class CSHighApi
    def initialize(id, key, host=CSLowApi::DEFAULT_HOST, version=CSLowApi::DEFAULT_VERSION)
      @lowapi = CSLowApi.new(id, key, host, version)
    end

    def call(category, command, params={})
      @lowapi.call(category, command, params).json['data']
    end

    # Env info

    def list_environments
      call('env', 'ListEnvironments')
    end

    def list_environments_with_state
      call('env', 'ListEnvironmentsWithStateExt')
    end

    def get_environment_status(envId)
      call('env', 'GetEnvironmentStateExt', {'EnvId' => envId})
    end

    def get_environment_status_list
      envs = list_environments
      envs.map{|env| get_environment_status(env['envId'])}
    end

    # General env actions

    def resume_environment(envId)
      call('env', 'ResumeEnvironment', {'EnvId' => envId})
    end

    def revert_environment(envId)
      call('env', 'RevertEnvironment', {'EnvId' => envId})
    end

    def delete_environment(envId)
      call('env', 'DeleteEnvironment', {'EnvId' => envId})
    end

    def extend_environment(envId)
      call('env', 'ExtendEnvironment', {'EnvId' => envId})
    end

    def postpone_inactivity(envId)
      call('env', 'PostponeInactivityAction', {'EnvId' => envId})
    end

    def suspend_environment(envId)
      call('env', 'SuspendEnvironment', {'EnvId' => envId})
    end

    def revert_environment_to_snapshot(envId, snapshotId)
      call('env', 'RevertEnvironmentToSnapshot', {'EnvId' => envId, 'SnapshotId' => snapshotId})
    end

    # Create env actions

    def list_templates
      call('env', 'ListTemplates')
    end

    def add_vm_from_template(envId, templateId, vm_name, vm_description)
      call('env', 'AddVmFromTemplate',
           {'EnvId' => envId, 'TemplateVmId' => templateId, 'VmName' => vm_name, 'VmDescription' => vm_description})
    end

    def create_ent_app_env_options(project_filter='', blueprint_filter='', env_policy_duration_filter='')
      call('env', 'CreateEntAppEnvOptions',
           {'ProjectFilter' => project_filter, 'BlueprintFilter' => blueprint_filter,
            'EnvironmentPolicyDurationFilter' => env_policy_duration_filter})
    end

    def create_ent_app_env(envPolicyId, snapshotId, env_new_name=nil, project_filter='',
        blueprint_filter='', env_policy_duration_filter='')
      call('env', 'CreateEntAppEnv', {'EnvironmentPolicyId' => envPolicyId,
                                      'SnapshotId' => snapshotId,
                                      'ProjectFilter' => project_filter,
                                      'BlueprintFilter' => blueprint_filter,
                                      'EnvironmentPolicyDurationFilter' => env_policy_duration_filter,
                                      'EnvironmentNewName' => env_new_name})
    end

    def create_empty_ent_app_env(env_name, project_name, description='none')
      call('env', 'CreateEmptyEntAppEnv',
           {'EnvName' => env_name, 'ProjectName' => project_name, 'Description' => description})
    end


    # Snapshots

    def get_snapshots(envId)
      call('env', 'GetSnapshots', {'EnvId' => envId})
    end

    def get_snapshot_info(snapshotId)
      call('env', 'GetSnapshotDetails', {'SnapshotId' => snapshotId})
    end

    def get_blueprints_for_publish(envId)
      call('env', 'GetBlueprintsForPublish', {'EnvId' => envId})
    end

    def mark_snapshot_default(envId, snapshotId)
      call('env', 'MarkSnapshotDefault', {'EnvId' => envId, 'SnapshotId' => snapshotId})
    end

    def ent_app_take_snapshot(envId, snapshot_name, description='', set_as_default=true)
      call('env', 'EntAppTakeSnapshot',
           {'EnvId' => envId, 'SnapshotName' => snapshot_name, 'Description' => description,
            'SetAsDefault' => (set_as_default ? 'true' : 'false')})
    end

    def ent_app_take_snapshot_to_new_blueprint(envId, snapshot_name, new_blueprint_name, description='')
      call('env', 'EntAppTakeSnapshotToNewBlueprint',
           {'EnvId' => envId,
            'SnapshotName' => snapshot_name,
            'NewBlueprintName' => new_blueprint_name,
            'Description' => description})
    end

    def ent_app_take_snapshot_to_existing_blueprint(envId, snapshot_name, otherBlueprintId, description='', set_as_default=true)
      call('env', 'EntAppTakeSnapshotToExistingBlueprint',
           {'EnvId' => envId,
            'SnapshotName' => snapshot_name,
            'OtherBlueprintId' => otherBlueprintId,
            'Desctiption' => description,
            'SetAsDefault' => (set_as_default ? 'true' : 'false')})
    end

    # VM actions

    def delete_vm(envId, vmId)
      call('env', 'DeleteVm', {'EnvId' => envId, 'VmId' => vmId})
    end

    def revert_vm(envId, vmId)
      call('env', 'RevertVm', {'EnvId' => envId, 'VmId' => vmId})
    end

    def reboot_vm(envId, vmId)
      call('env', 'RebootVm', {'EnvId' => envId, 'VmId' => vmId})
    end

    def execute_path(envId, vmId, path)
      call('env', 'ExecutePathExt', {'EnvId' => envId, 'VmId' => vmId, 'Path' => path})
    end

    def edit_machine_hardware(envId, vmId, numCpus=nil,mbRAM=nil, gbDisk=nil)
      call('env', 'EditMachineHardware',
           {'EnvId' => envId,
            'VmId' => vmId,
            'NumCpus' => numCpus,
            'MemorySizeMBs' => mbRAM,
            'DiskSizeGBs' => gbDisk})
    end

    def check_execution_id(envId, vmId, exec_id)
      call('env', 'CheckExecutionStatus', {'EnvId' => envId, 'VmId' => vmId, 'ExecutionId' => exec_id})
    end

    # CloudFolders

    def get_cloudfolders_info
      call('env', 'GetCloudFoldersInfo')
    end

    def mount(envId)
      call('env', 'Mount', {'EnvId' => envId})
    end

    def unmount(envId)
      call('env', 'Unmount', {'EnvId' => envId})
    end

    def mount_and_fetch_info(envId)
      call('env', 'MountAndFetchInfo', {'EnvId' => envId})
    end

    def mount_and_fetch_info_ext(envId)
      call('env', 'MountAndFetchInfoExt', {'EnvId' => envId})
    end

    def regenerate_cloudfolders_password
      call('env', 'RegenerateCloudfoldersPassword')
    end

    # Login

    def get_login_url(url)
      call('env', 'GetLoginUrl', {'Url' => url})
    end

    def who_am_i(userId)
      call('env', 'WhoAmI', {'UserId' => userId})
    end

    # RDP

    def get_remote_access(envId, vmId, isConsole='', desktopWidth='', desktopHeight='')
      call('env', 'GetRemoteAccessFile',
           {'EnvId' => envId,
            'VmId' => vmId,
            'IsConsole' => isConsole,
            'DesktopWidth' => desktopWidth,
            'DesktopHeight' => desktopHeight})
    end

    # Admin

    def list_allowed_commands
      call('admin', 'ListAllowedCommands')
    end
  end
end
