var express = require('express');
var router = express.Router();
var crypto = require('crypto');
var fs = require('fs');
var path = require('path');

var shasum = crypto.createHash('sha1');
//shasum.update("c518f228e213c172");
shasum.update("57288dc258d4aa5c");
var result = shasum.digest('hex');
var code = 0;

/* GET users listing. */
router.get('/', function(req, res, next) {
  res.send(result);
});

router.post('/', function(req, res, next) {
  // console.log(req.params);
  // console.log(req.body);
  // console.log(req.body.Msg.Content);
  var content = req.body.Msg.Content;
  // var encoded = new Buffer(content).toString('base64');
  // base64解码
  var decoded = new Buffer(content, 'base64').toString();
  // console.log(decoded);
  
  var decodedSessionParams = new Buffer(req.body.SessionParams, 'base64').toString();
  // console.log(decodedSessionParams);
  
  req.body.SessionParams = JSON.parse(decodedSessionParams);
  req.body.Msg.Content = JSON.parse(decoded);
  
  var preWriteString = JSON.stringify(req.body) + '\n';
  
  fs.appendFile(path.join(__dirname, '../post.txt'), preWriteString, function(){
	console.log('write into file');
  });
  
  var name = '';
  var age = '';
  
  var intent = JSON.parse(decoded).intent;
  // console.log("intent.text = " + intent.text);
  if(intent.text !== undefined) {
	  if(intent.category == "ISHANG.mHealth_demo" && intent.query == "我尚") {
		req.body.Msg.Content.intent.answer = "欢迎您使用我尚智能语音服务，请说出自己的名字";
		code = 1;
		res.json(req.body.Msg.Content);
	  } else if(code == 1) {
		name = intent.text;
		req.body.Msg.Content.intent.answer = '您的名字是' + name + '，请说出您的年龄';
		code = 2;
		res.json(req.body.Msg.Content);
	  } else if(code == 2) {
		age = intent.text;
		req.body.Msg.Content.intent.answer = '您的年龄是' + age + '，谢谢使用，再见';
		code = 3;
		res.json(req.body.Msg.Content);
	  }
  }
  
  // console.log("code = "+code);
	
  // res.json(req.body.Msg.Content);
});

module.exports = router;
