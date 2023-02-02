resource "aws_alb" "app_alb" {
  name               = "${var.app_name}-elb"
  internal           = false
  load_balancer_type = "application"
  subnets            = module.vpc.public_subnets
  security_groups    = [aws_security_group.web-access.id]
}

resource "aws_alb_target_group" "service-target-group" {
  name     = "${var.app_name}-tg"
  port     = 80
  protocol = "HTTP"
  target_type = "ip"
  vpc_id   = module.vpc.vpc_id
}

resource "aws_alb_listener" "http_listener" {
  load_balancer_arn = aws_alb.app_alb.id
  port              = "80"
  protocol          = "HTTP"

  default_action {
    target_group_arn = aws_alb_target_group.service-target-group.id
    type             = "forward"
  }
}