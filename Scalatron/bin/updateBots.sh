#!/usr/bin/env bash

## Ugly hacky script. Feel free to send a PR to make it better!

## some environment config
dir="$( cd "$( dirname "$0" )" && pwd )"
bots_root=$dir/../bots
clone_folder_name="myBot"
echo "Bots root located at: $bots_root" >&2

## load players from config file
source $dir/players.cfg
total_players=${#players[@]}

## now loop through the players array and update their bots
echo "Iterating over $((total_players / 2)) players to add their bots to the game"
## for i in "${!players[@]}"
for (( i=0; i<=$(( $total_players -1 )); i=i+2 ))
do
   name=${players[$i]}
   git_repo=${players[$i+1]}
   echo "Updating bot for $name located at $git_repo"
   ## go to player's folder (create if it doesn't exist) 
   cd $bots_root
   mkdir -p $name
   cd $name
   ## remove any previous clone and clone new repo
   rm -rf $clone_folder_name
   git clone $git_repo $clone_folder_name
   ## build project and copy jar to player's folder
   cd $clone_folder_name
   sbt assembly
   cp target/scala-2.11/ScalatronBot.jar ../
done
