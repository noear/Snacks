Pod::Spec.new do |s|
  s.name        = 'Snacks'
  s.version     = '1.0.0'
  s.summary     = 'Simple XML/JSON parsing in Swift'
  s.homepage    = 'https://github.com/noear/Snacks'
  s.license     = { type: 'MIT' }
  s.authors     = { 'noear' => 'noear@live.cn' }

  s.requires_arc = true
  s.pod_target_xcconfig = { 'SWIFT_VERSION' => '3.0' }

  s.osx.deployment_target = '10.9'
  s.ios.deployment_target = '8.0'
  s.watchos.deployment_target = '2.0'
  s.tvos.deployment_target = '9.0'

  s.source = { git: 'https://github.com/noear/Snacks.git',
               tag: s.version }
  s.source_files = 'ios/Snacks/Snacks/src/*.swift'
end
