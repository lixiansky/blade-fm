(function($) {
    var repeat = localStorage.repeat || 0,
    shuffle = localStorage.shuffle || 'true',
    continous = true,
    autoplay = true,
    playlist = [{
        title: 'With or without you',
        artist: 'U2',
        album: 'The Best Of 1980-1990',
        cover: 'http://aceace.qiniudn.com/fm/cover/The_Best_Of_1980-1990.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/U2 - with or without you.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/U2 - with or without you.OGG'
    },
    {
        title: '男人的错',
        artist: '陈奕迅',
        album: '七',
        cover: 'http://aceace.qiniudn.com/fm/cover/qi.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 男人的错.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 男人的错.OGG'
    },
    {
        title: '当爱已成往事',
        artist: '张国荣',
        album: '世纪10星 - 永恒篇',
        cover: 'http://aceace.qiniudn.com/fm/cover/shiji10xing.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/张国荣 - 当爱已成往事.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/张国荣 - 当爱已成往事.OGG'
    },
    {
        title: 'Say Hello',
        artist: 'Rosie Thomas',
        album: '《All the Way from Michigan Not Mars》',
        cover: 'http://aceace.qiniudn.com/fm/cover/ATWFMNM.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/Rosie Thomas - Say Hello.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/Rosie Thomas - Say Hello.OGG'
    },
    {
        title: 'Fearless',
        artist: 'Taylor Swift',
        album: '《Fearless》',
        cover: 'http://aceace.qiniudn.com/fm/cover/fearless.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/Taylor Swift - Fearless.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/Taylor Swift - Fearless.OGG'
    },
    {
        title: '圣诞结',
        artist: '陈奕迅',
        album: '你的陈奕迅 国语精选',
        cover: 'http://aceace.qiniudn.com/fm/cover/nidechenyixun.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 圣诞结.mp3',
        OGG: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 圣诞结.OGG'
    },
    {
        title: '想自由',
        artist: '林宥嘉',
        album: '美妙生活',
        cover: 'http://aceace.qiniudn.com/fm/cover/meimiaoshenghuo.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/林宥嘉 - 想自由.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/林宥嘉 - 想自由.OGG'
    },
    {
        title: '红玫瑰',
        artist: '陈奕迅',
        album: '1997-2007 跨世纪国语精选',
        cover: 'http://aceace.qiniudn.com/fm/cover/cyx1997-2007.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 红玫瑰.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 红玫瑰.OGG'
    },
    {
        title: 'Let Her Go',
        artist: 'Passenger',
        album: 'All The Little Lights',
        cover: 'http://aceace.qiniudn.com/fm/cover/All_The_Little_Lights.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/Passenger - Let Her Go.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/Passenger - Let Her Go.OGG'
    },
    {
        title: '婚礼的祝福',
        artist: '陈奕迅',
        album: '张学友',
        cover: 'http://aceace.qiniudn.com/fm/cover/hunlidezhufu.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 婚礼的祝福.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 婚礼的祝福.OGG'
    },
    {
        title: '飘雪',
        artist: '韩雪',
        album: '飘雪',
        cover: 'http://aceace.qiniudn.com/fm/cover/piaoxue.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/韩雪 - 飘雪.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/韩雪 - 飘雪.OGG'
    },
    {
        title: 'Take Me To Your Heart',
        artist: 'Michael Learns To Rock',
        album: '《Everlasting Love Songs》',
        cover: 'http://aceace.qiniudn.com/fm/cover/Everlasting_Love_Songs.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/Michael Learns To Rock - Take Me To Your Heart.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/Michael Learns To Rock - Take Me To Your Heart.OGG'
    },
    {
        title: '突然想起你',
        artist: '萧亚轩',
        album: '《同名专辑》',
        cover: 'http://aceace.qiniudn.com/fm/cover/xiaoyaxuan.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/萧亚轩 - 突然想起你.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/萧亚轩 - 突然想起你.OGG'
    },
    {
        title: '让我一次爱个够',
        artist: '庾澄庆',
        album: '《让我一次爱个够》',
        cover: 'http://aceace.qiniudn.com/fm/cover/rangwoyiciagegou.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/庾澄庆 - 让我一次爱个够.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/庾澄庆 - 让我一次爱个够.OGG'
    },
    {
        title: '情非得已',
        artist: '庾澄庆',
        album: '《死都要18岁》',
        cover: 'http://aceace.qiniudn.com/fm/cover/daosidouyao18sui.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/庾澄庆 - 情非得已.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/庾澄庆 - 情非得已.OGG'
    },
    {
        title: '特别的爱给特别的你',
        artist: '伍思凯',
        album: '特别的爱给特别的你',
        cover: 'http://aceace.qiniudn.com/fm/cover/tbdagtbdn.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/伍思凯 - 特别的爱给特别的你.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/伍思凯 - 特别的爱给特别的你.OGG'
    },
    {
        title: 'Lemon Tree',
        artist: 'Fools Garden',
        album: '《High Time》',
        cover: 'http://aceace.qiniudn.com/fm/cover/fools_garden.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/Fools Garden - Lemon Tree.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/Fools Garden - Lemon Tree.OGG'
    },
    {
        title: '答案',
        artist: '杨坤&郭采洁',
        album: '《答案》',
        cover: 'http://aceace.qiniudn.com/fm/cover/daan.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/杨坤&郭采洁 - 答案.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/杨坤&郭采洁 - 答案.OGG'
    },
    {
        title: 'Be What You Wanna Be',
        artist: 'Darin Zanyar',
        album: '《Darin》',
        cover: 'http://aceace.qiniudn.com/fm/cover/darin.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/Darin Zanyar - Be What You Wanna Be.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/Darin Zanyar - Be What You Wanna Be.OGG'
    },
    {
        title: '苦瓜',
        artist: '陈奕迅',
        album: '《Stranger Under My Skin》',
        cover: 'http://aceace.qiniudn.com/fm/cover/Stranger_Under_My_Skin.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 苦瓜.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 苦瓜.OGG'
    },
    {
        title: '十年',
        artist: '陈奕迅',
        album: '《The Best Moment》',
        cover: 'http://aceace.qiniudn.com/fm/cover/The_Best_Moment.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 十年.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 十年.OGG'
    },
    {
        title: '认真的雪',
        artist: '薛之谦',
        album: '《同名专辑》',
        cover: 'http://aceace.qiniudn.com/fm/cover/xuezhiqian.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/薛之谦 - 认真的雪.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/薛之谦 - 认真的雪.OGG'
    },
    {
        title: '我',
        artist: '张国荣',
        album: '《Miss You Much, Leslie》',
        cover: 'http://aceace.qiniudn.com/fm/cover/Miss_You_ Much_ Leslie.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/张国荣 - 我.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/张国荣 - 我.OGG'
    },
    {
        title: 'Long Lost Penpal',
        artist: 'Hello Saferide',
        album: '《Introducing... Hello Saferide》',
        cover: 'http://aceace.qiniudn.com/fm/cover/Introducing..._Hello_Saferide.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/Hello Saferide - Long Lost Penpal.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/Hello Saferide - Long Lost Penpal.OGG'
    },
    {
        title: '你的背包',
        artist: '陈奕迅',
        album: '《Special Thanks To》',
        cover: 'http://aceace.qiniudn.com/fm/cover/Special_Thanks_To.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 你的背包.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 你的背包.OGG'
    },
    {
        title: '不要说话',
        artist: '陈奕迅',
        album: '《不想放手》',
        cover: 'http://aceace.qiniudn.com/fm/cover/buxiangfangshou.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 不要说话.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 不要说话.OGG'
    },
    {
        title: '礼物',
        artist: '刘力扬',
        album: '《转寄刘力扬》',
        cover: 'http://aceace.qiniudn.com/fm/cover/zhuanjiliuliyang.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/刘力扬 - 礼物.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/刘力扬 - 礼物.OGG'
    },
    {
        title: '良人',
        artist: '邓福如',
        album: '《天空岛》',
        cover: 'http://aceace.qiniudn.com/fm/cover/tiankongdao.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/邓福如 - 良人.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/邓福如 - 良人.OGG'
    },
    {
        title: '如果爱忘了',
        artist: '戚薇',
        album: '《如果爱忘了》',
        cover: 'http://aceace.qiniudn.com/fm/cover/rgawl.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/戚薇 - 如果爱忘了.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/戚薇 - 如果爱忘了.OGG'
    },
    {
        title: '过火',
        artist: '张信哲',
        album: '《宽容》',
        cover: 'http://aceace.qiniudn.com/fm/cover/kuanrong.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/张信哲 - 过火.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/张信哲 - 过火.OGG'
    },
    {
        title: '孤独患者',
        artist: '陈奕迅',
        album: '《？》',
        cover: 'http://aceace.qiniudn.com/fm/cover/gdhz.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 孤独患者.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/陈奕迅 - 孤独患者.OGG'
    },
    {
        title: 'Love To Be Loved By You',
        artist: 'Marc Terenzi',
        album: '《Awesome》',
        cover: 'http://aceace.qiniudn.com/fm/cover/awesome.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/Marc Terenzi - Love To Be Loved By You.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/Marc Terenzi - Love To Be Loved By You.OGG'
    },
    {
        title: '静静的',
        artist: '庾澄庆',
        album: '《戒不掉》',
        cover: 'http://aceace.qiniudn.com/fm/cover/jiebudiao.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/庾澄庆 - 静静的.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/庾澄庆 - 静静的.OGG'
    },
    {
        title: '眼泪的错觉',
        artist: '王露凝',
        album: '《柏拉图式的爱》',
        cover: 'http://aceace.qiniudn.com/fm/cover/yldcj.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/王露凝 - 眼泪的错觉.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/王露凝 - 眼泪的错觉.OGG'
    },
    {
        title: '爱如潮水',
        artist: '张信哲',
        album: '《心事》',
        cover: 'http://aceace.qiniudn.com/fm/cover/xinshi.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/张信哲 - 爱如潮水.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/张信哲 - 爱如潮水.OGG'
    },
    {
        title: '新不了情',
        artist: '万芳',
        album: '同名电影《新不了情》',
        cover: 'http://aceace.qiniudn.com/fm/cover/xblq.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/万芳 - 新不了情.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/万芳 - 新不了情.OGG'
    },
    {
        title: '叶子',
        artist: '阿桑',
        album: '《受了点伤》',
        cover: 'http://aceace.qiniudn.com/fm/cover/slds.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/阿桑 - 叶子.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/阿桑 - 叶子.OGG'
    },
    {
        title: '爱我别走',
        artist: '张震岳',
        album: '《秘密基地》',
        cover: 'http://aceace.qiniudn.com/fm/cover/mimijidi.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/张震岳 - 爱我别走.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/张震岳 - 爱我别走.OGG'
    },
    {
        title: 'Anyone of Us',
        artist: 'Gareth Gates',
        album: '《What My Heart Wants To Say》',
        cover: 'http://aceace.qiniudn.com/fm/cover/What_My_Heart_Wants_To_Say.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/Gareth Gates - Anyone of Us.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/Gareth Gates - Anyone of Us.OGG'
    },
    {
        title: 'Free Loop',
        artist: 'Daniel Powter',
        album: '《Daniel Powter》',
        cover: 'http://aceace.qiniudn.com/fm/cover/Daniel_Powter.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/Daniel Powter - Free Loop.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/Daniel Powter - Free Loop.OGG'
    },
    {
        title: 'Whatever You Like',
        artist: 'Anya Marina',
        album: '《Whatever You Like》',
        cover: 'http://aceace.qiniudn.com/fm/cover/Whatever_You_Like.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/Anya Marina - Whatever You Like.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/Anya Marina - Whatever You Like.OGG'
    },
    {
        title: '我总是一个人在练习一个人',
        artist: '林宥嘉',
        album: '《美妙生活》',
        cover: 'http://aceace.qiniudn.com/fm/cover/meimiaoshenghuo.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/林宥嘉 - 我总是一个人在练习一个人.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/林宥嘉 - 我总是一个人在练习一个人.OGG'
    },
    {
        title: 'And Then You',
        artist: 'Greg Laswell',
        album: '《Three Flights From Alto Nido》',
        cover: 'http://aceace.qiniudn.com/fm/cover/Three_Flights_From_Alto_Nido.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/Greg Laswell - And Then You.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/Greg Laswell - And Then You.OGG'
    },
    {
        title: 'The Heart Never Lies',
        artist: 'Mcfly',
        album: '《The Heart Never Lies》',
        cover: 'http://aceace.qiniudn.com/fm/cover/The_Heart_Never_Lies.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/Mcfly - The Heart Never Lies.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/Mcfly - The Heart Never Lies.OGG'
    },
    {
        title: '分开旅行',
        artist: '刘若英',
        album: '《我的失败与伟大》',
        cover: 'http://aceace.qiniudn.com/fm/cover/wdsbywd.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/刘若英 - 分开旅行.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/刘若英 - 分开旅行.OGG'
    },
    {
        title: 'Cry On My Shoulder',
        artist: 'Deutschland Sucht Den Superstar',
        album: '《United》',
        cover: 'http://aceace.qiniudn.com/fm/cover/united.jpg',
        mp3: 'http://aceace.qiniudn.com/fm/music/Deutschland Sucht Den Superstar - Cry On My Shoulder.mp3',
        ogg: 'http://aceace.qiniudn.com/fm/music/Deutschland Sucht Den Superstar - Cry On My Shoulder.OGG'
    }];
    for (var i = 0; i < playlist.length; i++) {
        var item = playlist[i];
        $('#playlist').append('<li>' + item.artist + ' - ' + item.title + '</li>')
    };
    var time = new Date(),
    currentTrack = shuffle === 'true' ? time.getTime() % playlist.length: 0,
    trigger = false,
    audio,
    timeout,
    isPlaying,
    playCounts;
    var play = function() {
        audio.play();
        $('.playback').addClass('playing');
        timeout = setInterval(updateProgress, 500);
        isPlaying = true
    };
    var pause = function() {
        audio.pause();
        $('.playback').removeClass('playing');
        clearInterval(updateProgress);
        isPlaying = false
    };
    var setProgress = function(value) {
        var currentSec = parseInt(value % 60) < 10 ? '0' + parseInt(value % 60) : parseInt(value % 60),
        ratio = value / audio.duration * 100;
        $('.timer').html(parseInt(value / 60) + ':' + currentSec);
        $('.musicprogress .pace').css('width', ratio + '%');
        $('.musicprogress .slider a').css('left', ratio + '%')
    };
    var updateProgress = function() {
        setProgress(audio.currentTime)
    };
    $('.musicprogress .slider').slider({
        step: 0.1,
        slide: function(event, ui) {
            $(this).addClass('enable');
            setProgress(audio.duration * ui.value / 100);
            clearInterval(timeout)
        },
        stop: function(event, ui) {
            audio.currentTime = audio.duration * ui.value / 100;
            $(this).removeClass('enable');
            timeout = setInterval(updateProgress, 500)
        }
    });
    var setVolume = function(value) {
        audio.volume = localStorage.volume = value;
        $('.volume .pace').css('width', value * 100 + '%');
        $('.volume .slider a').css('left', value * 100 + '%')
    };
    var volume = localStorage.volume || 0.5;
    $('.volume .slider').slider({
        max: 1,
        min: 0,
        step: 0.01,
        value: volume,
        slide: function(event, ui) {
            setVolume(ui.value);
            $(this).addClass('enable');
            $('.mute').removeClass('enable')
        },
        stop: function() {
            $(this).removeClass('enable')
        }
    }).children('.pace').css('width', volume * 100 + '%');
    $('.mute').click(function() {
        if ($(this).hasClass('enable')) {
            setVolume($(this).data('volume'));
            $(this).removeClass('enable')
        } else {
            $(this).data('volume', audio.volume).addClass('enable');
            setVolume(0)
        }
    });
    var switchTrack = function(i) {
        if (i < 0) {
            track = currentTrack = playlist.length - 1
        } else if (i >= playlist.length) {
            track = currentTrack = 0
        } else {
            track = i
        };
        $('audio').remove();
        loadMusic(track);
        if (isPlaying == true) play()
    };
    var shufflePlay = function() {
        var time = new Date(),
        lastTrack = currentTrack;
        currentTrack = time.getTime() % playlist.length;
        if (lastTrack == currentTrack)++currentTrack;
        switchTrack(currentTrack)
    };
    var ended = function() {
        pause();
        audio.currentTime = 0;
        playCounts++;
        if (continous == true) isPlaying = true;
        if (repeat == 1) {
            play()
        } else {
            if (shuffle === 'true') {
                shufflePlay()
            } else {
                if (repeat == 2) {
                    switchTrack(++currentTrack)
                } else {
                    if (currentTrack < playlist.length) switchTrack(++currentTrack)
                }
            }
        }
    };
    var beforeLoad = function() {
        var endVal = this.seekable && this.seekable.length ? this.seekable.end(0) : 0;
        $('.musicprogress .loaded').css('width', (100 / (this.duration || 1) * endVal) + '%')
    };
    var afterLoad = function() {
        if (autoplay == true) play()
    };
    var loadMusic = function(i) {
        var item = playlist[i],
        newaudio = $('<audio>').html('<source src="' + item.mp3 + '"><source src="' + item.ogg + '">').appendTo('#player');
        $('.cover').html('<img src="' + item.cover + '" alt="' + item.album + '">');
        $('.tag').html('<strong>' + item.title + '</strong><span class="artist">' + item.artist + '</span><span class="album">' + item.album + '</span>');
        $('#playlist li').removeClass('playing').eq(i).addClass('playing');
        audio = newaudio[0];
        audio.volume = $('.mute').hasClass('enable') ? 0 : volume;
        audio.addEventListener('musicprogress', beforeLoad, false);
        audio.addEventListener('durationchange', beforeLoad, false);
        audio.addEventListener('canplay', afterLoad, false);
        audio.addEventListener('ended', ended, false)
    };
    loadMusic(currentTrack);
    $('.playback').on('click',
    function() {
        if ($(this).hasClass('playing')) {
            pause()
        } else {
            play()
        }
    });
    $('.rewind').on('click',
    function() {
        if (shuffle === 'true') {
            shufflePlay()
        } else {
            switchTrack(--currentTrack)
        }
    });
    $('.fastforward').on('click',
    function() {
        if (shuffle === 'true') {
            shufflePlay()
        } else {
            switchTrack(++currentTrack)
        }
    });
    $('#playlist li').each(function(i) {
        var _i = i;
        $(this).on('click',
        function() {
            switchTrack(_i)
        })
    });
    if (shuffle === 'true') $('.shuffle').addClass('enable');
    if (repeat == 1) {
        $('.repeat').addClass('once')
    } else if (repeat == 2) {
        $('.repeat').addClass('all')
    };
    $('.repeat').on('click',
    function() {
        if ($(this).hasClass('once')) {
            repeat = localStorage.repeat = 2;
            $(this).removeClass('once').addClass('all')
        } else if ($(this).hasClass('all')) {
            repeat = localStorage.repeat = 0;
            $(this).removeClass('all')
        } else {
            repeat = localStorage.repeat = 1;
            $(this).addClass('once')
        }
    });
    $('.shuffle').on('click',
    function() {
        if ($(this).hasClass('enable')) {
            shuffle = localStorage.shuffle = 'false';
            $(this).removeClass('enable')
        } else {
            shuffle = localStorage.shuffle = 'true';
            $(this).addClass('enable')
        }
    })
})(jQuery);