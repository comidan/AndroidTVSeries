package test.tvdb.dev.com.tvdb_test;

import java.util.ArrayList;

/**
 * Created by daniele on 27/04/2015.
 */
public class ShowDate
{
    private ArrayList<ArrayList<Boolean>> watches;

    public ShowDate(ArrayList<ArrayList<Boolean>> watches)
    {
        this.watches=watches;
    }

    private class ShowIndex
    {
        int season;
        int episode;

        public ShowIndex(int season,int episode)
        {
            this.season=season;
            this.episode=episode;
        }
    }

    private ShowIndex getNextIndexShowDate()
    {
        int i,j;
        try
        {
            for(j=0;j<watches.size();j++)
                for(i=0;i<watches.get(j).size();i++)
                    if(!watches.get(j).get(i))
                        return new ShowIndex(j,i+1);
            return new ShowIndex(-1,-1);

        }
        catch (IndexOutOfBoundsException exc)
        {
               return new ShowIndex(-1,-1);
        }

    }

    void fillMetaData(MetaEpisode metaData,ArrayList<Season> tmpSeasons)
    {
        ShowIndex toBeSeen=getNextIndexShowDate();
        if(toBeSeen.episode==-1)
            metaData.full=true;
        else
            metaData.full=false;
        metaData.season=toBeSeen.season;
        metaData.index=toBeSeen.episode;
        if(tmpSeasons.get(tmpSeasons.size()-1).getSeasonNumber()==0)
            metaData.seasonOffset=1;
        else
            metaData.seasonOffset=0;
    }

}
