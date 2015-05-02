package test.tvdb.dev.com.tvdb_test;

import java.util.ArrayList;

/**
 * Created by daniele on 27/04/2015.
 */
public class ShowDate
{
    private ArrayList<Boolean> watches;

    public ShowDate(ArrayList<Boolean> watches)
    {
        this.watches=watches;
    }

    private int getNextIndexShowDate()
    {
        int i=0;
        try
        {
            while (watches.get(i))
                i++;
            return i;
        }
        catch (IndexOutOfBoundsException exc)
        {
               return -1;
        }

    }

    void fillMetaData(MetaEpisode metaData,ArrayList<Season> tmpSeasons)
    {
        int toBeSeen=getNextIndexShowDate();
        if(toBeSeen==-1)
            metaData.full=true;
        else
            metaData.full=false;
        for(int i=0,tmpCount=0;i<tmpSeasons.size();i++) {
            if (toBeSeen>=tmpCount&&toBeSeen<tmpCount+tmpSeasons.get(i).getTotEpisodes()) {
                metaData.season=tmpSeasons.get(i).getSeasonNumber();
                for(int j=i-1;j>=0;j--)
                    toBeSeen-=tmpSeasons.get(j).getTotEpisodes();
                System.out.println(toBeSeen);
                toBeSeen++;
                metaData.index=toBeSeen;
                if(tmpSeasons.get(0).getSeasonNumber()==0)
                    metaData.seasonOffset=1;
                else
                    metaData.seasonOffset=0;
                break;
            }
            tmpCount+=tmpSeasons.get(i).getTotEpisodes();
        }
    }

}
