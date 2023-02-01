resource "aws_iam_role" "task-definition-execution-role" {
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

resource "aws_iam_policy" "execution-role-policy" {
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
          aws_secretsmanager_secret.database_uri.arn
        ]
      }
    ]
  })
}

resource "aws_iam_policy_attachment" "execution-role-policy-attachment" {
  name       = "${var.app_name}-execution-role-policy-attachment"
  roles      = [aws_iam_role.task-definition-execution-role.name]
  policy_arn = aws_iam_policy.execution-role-policy.arn
}


resource "aws_iam_role" "task-definition-task-role" {
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

resource "aws_iam_policy" "task-role-policy" {
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

resource "aws_iam_policy_attachment" "task-role-policy-attachment" {
  name       = "${var.app_name}-policy-attachment"
  roles      = [aws_iam_role.task-definition-task-role.name]
  policy_arn = aws_iam_policy.task-role-policy.arn
}
