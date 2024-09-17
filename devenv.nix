{
  pkgs,
  lib,
  config,
  inputs,
  ...
}:

{
  packages = with pkgs; [
    procps
    git
    leiningen
    openjdk11
    cider
    babashka
    pipx
    apacheKafka
  ];

  # https://devenv.sh/languages/
  languages.clojure.enable = true;
  languages.java.enable = true;

  # https://devenv.sh/processes/
  processes.server-start.exec = ''
    pushd ${pkgs.apacheKafka}
    bin/kafka-server-start.sh config/kraft/server.properties
    popd
  '';

  # https://devenv.sh/scripts/
  scripts.produce-tasks.exec = "lein run resources/kraft/server.properties tasks";

  scripts.generate-cluster-id.exec = ''
    pushd ${pkgs.apacheKafka}
    KAFKA_CLUSTER_ID="$(bin/kafka-storage.sh random-uuid)"
    bin/kafka-storage.sh format -t KAFKA_CLUSTER_ID -c config/kraft/server.properties
    popd
  '';

  enterShell = ''
    export JAVA_HOME=${pkgs.openjdk11}
    export PATH="${pkgs.apacheKafka}:$PATH"
    pipx install kaskade
  '';

  # https://devenv.sh/tests/
  #enterTest = ''git --version | grep --color=auto "${pkgs.git.version}"'';

  # https://devenv.sh/pre-commit-hooks/
  # pre-commit.hooks.shellcheck.enable = true;

  # See full reference at https://devenv.sh/reference/options/
}
