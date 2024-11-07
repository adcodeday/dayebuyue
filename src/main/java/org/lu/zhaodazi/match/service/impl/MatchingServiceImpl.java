package org.lu.zhaodazi.match.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.qrcode.QrConfig;
import lombok.extern.slf4j.Slf4j;
import org.lu.zhaodazi.common.config.RabbitMQConfig;
import org.lu.zhaodazi.match.domain.MatchAdapter;
import org.lu.zhaodazi.match.domain.dto.Match;
import org.lu.zhaodazi.match.domain.entity.MatchingCondition;
import org.lu.zhaodazi.common.domain.vo.entity.CustomMessage;
import org.lu.zhaodazi.match.service.MatchingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
@Slf4j
@Service
//并不负责状态的转换
public class MatchingServiceImpl implements MatchingService {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    MatchAdapter matchAdapter;


    private Integer matchingCapity=70;
    private Integer matchingNumber=0;

    private HashMap<String, HashSet<Match>> hashMap= new HashMap<>();
    private HashMap<String,Match> hashMap1= new HashMap<>();

    public HashMap<String, HashSet<Match>> getHashMap() {
        return hashMap;
    }

    @RabbitListener(queues = RabbitMQConfig.MATCH_START_QUEUE)
    @Override
    public void getMessage(CustomMessage message) {
        try {
            log.info("接收到消息：{}",message.getData());
            if(ObjectUtil.isEmpty(message)){
                return;
            }
            if(message.getType().equals(CustomMessage.START_MATCH)){
                log.info("消息类型为START_MATCH");
                Match match = matchAdapter.buildMatch((String) message.getData());
                findCompanion(match);
            }else if(message.getType().equals(CustomMessage.CANCEL_MATCH)){
                log.info("消息类型为CANCEL_MATCH");
                cancelMatching((String) message.getData());
            }else {
                log.info("消息类型为未知");
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }


    private void findCompanion(Match matching){
        HashSet<Match> matchs = hashMap.get(matching.getMatchingCondition().getCodeWord());
        if(matchs==null||matchs.isEmpty()){
            addMatchingToSet(matching);
            return;
        }
        Iterator<Match> iterator = matchs.iterator();
        while (iterator.hasNext()){
            Match matching1=iterator.next();
            if(conditionSuitable(matching,matching1)){
                removeMatchingFromSet(matching1);
                String[] channelIds=new String[2];
                channelIds[0]=matching.getChannelId();
                channelIds[1]=matching1.getChannelId();
                log.info("匹配成功{}",channelIds);
                rabbitTemplate.convertAndSend(RabbitMQConfig.MATCH_EX, RabbitMQConfig.MATCH_FINISH_BIND,CustomMessage.matchSuc(channelIds));
                return;
            }
        }
        addMatchingToSet(matching);
    }
    private void cancelMatching(String channelId){
        Match matching = hashMap1.get(channelId);
        removeMatchingFromSet(matching);
    }

    public Integer getMatchingNumber(){
        return hashMap.size();
    }
    private void addMatchingToSet(Match matching){
        matchingNumber++;
        hashMap1.put(matching.getChannelId(),matching);
        HashSet<Match> matchings = hashMap.computeIfAbsent(matching.getMatchingCondition().getCodeWord(), k -> new HashSet<>());
        matchings.add(matching);
    }
    private void  removeMatchingFromSet(Match matching){
        matchingNumber--;
        hashMap1.remove(matching.getChannelId());
        HashSet<Match> matchings = hashMap.get(matching.getMatchingCondition().getCodeWord());
        if(matchings==null){
            return;
        }
        matchings.remove(matching);
        if(matchings.isEmpty()){
            hashMap.remove(matching.getMatchingCondition().getCodeWord());
        }
    }
    private boolean conditionSuitable(Match m1,Match m2){
        MatchingCondition mc1 = m1.getMatchingCondition();
        MatchingCondition mc2 = m2.getMatchingCondition();
        return (mc1.getSex()==mc2.getWantSex()||mc2.getWantSex()==3)&&(mc2.getSex()==mc1.getWantSex()||mc1.getWantSex()==3)&&(mc1.getProvince()==mc2.getWantProvince()||mc2.getWantProvince()==35)&&(mc2.getProvince()==mc1.getWantProvince()||mc1.getWantProvince()==35);
    }
}
