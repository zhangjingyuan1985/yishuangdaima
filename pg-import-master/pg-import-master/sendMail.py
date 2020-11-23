import smtplib
from email.mime.text import MIMEText
#发送方
mail_host = 'smtp.qq.com'
mail_user = '1009811011@qq.com'
mail_pass = 'imoqtiysgvhobdge'

sender = '1009811011@qq.com'
receivers = ['tangpf@sutpc.com']
def sendMail(msg,title):
    message = MIMEText(msg,'plain','utf-8');
    message['Subject'] = title;
    message['From'] = sender;
    message['To'] = receivers[0];
    try:
        smtpObj = smtplib.SMTP()
        # 连接到服务器
        smtpObj.connect(mail_host, 25)
        # 登录到服务器
        smtpObj.login(mail_user, mail_pass)
        # 发送
        smtpObj.sendmail(
            sender, receivers, message.as_string())
        # 退出
        smtpObj.quit()
        print('success')
    except smtplib.SMTPException as e:
        print('error', e)  # 打印错误
# if __name__ == '__main__':
#     sendMail("aaa", "测试")