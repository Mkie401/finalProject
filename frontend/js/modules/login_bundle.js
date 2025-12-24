(function loadLoginScripts() {
  const scripts = [
    'js/modules/captcha.js',
    'js/modules/account_common.js',
    'js/modules/memlogin.js',
    'js/modules/forgetpassword.js'
  ];

  const head = document.head || document.getElementsByTagName('head')[0];

  scripts.forEach(src => {
    const script = document.createElement('script');
    script.src = src;
    script.defer = true;
    head.appendChild(script);
  });
})();
