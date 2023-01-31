resource "mongodbatlas_project" "project" {
  name   = var.app_name
  org_id = var.mongo_org_id
}

resource "mongodbatlas_database_user" "db_user" {
  username           = var.db_username
  password           = var.db_password
  project_id         = mongodbatlas_project.project.id
  auth_database_name = "admin"

  roles {
    role_name     = "readWrite"
    database_name = var.app_name
  }

  roles {
    role_name     = "readAnyDatabase"
    database_name = "admin"
  }

  scopes {
    name = mongodbatlas_advanced_cluster.database.name
    type = "CLUSTER"
  }
}

resource "mongodbatlas_project_ip_access_list" "database_ip_list" {
  project_id = mongodbatlas_project.project.id
  cidr_block = "0.0.0.0/0"
  # This can be restricted
  comment    = "Allow access from anywhere"
}

resource "mongodbatlas_advanced_cluster" "database" {
  project_id                     = mongodbatlas_project.project.id
  name                           = "${var.app_name}-cluster"
  cluster_type                   = "REPLICASET"
  termination_protection_enabled = false

  replication_specs {
    region_configs {
      electable_specs {
        instance_size = "M0"
      }
      provider_name         = "TENANT"
      backing_provider_name = "AWS"
      region_name           = replace(upper(var.region), "-", "_")
      priority              = 7
    }
  }
}
