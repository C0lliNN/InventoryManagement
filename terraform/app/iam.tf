resource "aws_iam_role" "task_definition_execution_role" {
  name               = "${var.app_name}-execution-role"
  assume_role_policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Action" : "sts:AssumeRole",
        "Principal" : {
          "Service" : "ecs-tasks.amazonaws.com"
        },
        "Effect" : "Allow",
        "Sid" : ""
      }
    ]
  })
}

resource "aws_iam_policy" "execution_role_policy" {
  name        = "${var.app_name}-execution-role-policy"
  description = "Allow ECR, Cloudwatch and Secrets Manager Access Access"
  policy      = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Sid" : "Stmt1674840832992",
        "Action" : "ecr:*",
        "Effect" : "Allow",
        "Resource" : "*"
      },
      {
        "Sid": "Stmt1674841846576",
        "Action": "cloudwatch:*",
        "Effect": "Allow",
        "Resource": "*"
      },
      {
        "Sid": "Stmt1674841846578",
        "Action": "logs:*",
        "Effect": "Allow",
        "Resource": "*"
      },
      {
        "Action": [
          "secretsmanager:GetSecretValue"
        ],
        "Effect": "Allow",
        "Resource": [
          aws_secretsmanager_secret.mongodb_connection_string.arn
        ]
      }
    ]
  })
}

resource "aws_iam_policy_attachment" "execution_role_policy_attachment" {
  name       = "${var.app_name}-execution-role-policy-attachment"
  roles      = [aws_iam_role.task_definition_execution_role.name]
  policy_arn = aws_iam_policy.execution_role_policy.arn
}


resource "aws_iam_role" "task_definition_task_role" {
  name               = "${var.app_name}-role"
  assume_role_policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Action" : "sts:AssumeRole",
        "Principal" : {
          "Service" : "ecs-tasks.amazonaws.com"
        },
        "Effect" : "Allow",
        "Sid" : ""
      }
    ]
  })
}

resource "aws_iam_policy" "task_role_policy" {
  name        = "${var.app_name}-policy"
  description = "Allow full S3 Access"
  policy      = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Sid" : "Stmt1674840832992",
        "Action" : "s3:*",
        "Effect" : "Allow",
        "Resource" : "*"
      }
    ]
  })
}

resource "aws_iam_policy_attachment" "task_role_policy_attachment" {
  name       = "${var.app_name}-policy-attachment"
  roles      = [aws_iam_role.task_definition_task_role.name]
  policy_arn = aws_iam_policy.task_role_policy.arn
}
